package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.server.GameManager;
import it.polimi.ingsw.server.GameRoom;
import it.polimi.ingsw.virtualView.VirtualView;
import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.dto.PlayerDTO;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/** Socket-based client handler that manages communication, matchmaking, and in-game message forwarding. */
public class SocketClientHandler implements ClientConnection, Runnable {

    private final Socket socket;
    private final GameManager gameManager;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private VirtualView virtualView;
    private AtomicBoolean active = new AtomicBoolean(true);
    private final Object streamLock = new Object();
    private String nickname;
    private Integer lastMatchPlayerCount;

    private MatchmakingState matchmakingState;

    /** Constructs the handler and initializes I/O streams. @param socket client socket @param gameManager game manager instance */
    public SocketClientHandler(Socket socket, GameManager gameManager) throws IOException {
        this.socket = socket;
        this.gameManager = gameManager;
        this.matchmakingState = new MatchmakingState(this, gameManager);

        this.socket.setSoTimeout(10000);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname(){
        return this.nickname;
    }


    /** Associates a VirtualView to forward in-game messages. @param virtualView virtual view */
    @Override
    public void setVirtualView(VirtualView virtualView) {
        this.virtualView = virtualView;
        // When view is set, we are no longer in matchmaking.
        this.matchmakingState = null;
        //in case vView changed after check in handleDisconnection
        if (!this.active.get()) {
            virtualView.handleDisconnection();
        }
    }

    private void sendMessage(ServerMessage message) {
        if (active.get()) {
            try {
                synchronized (streamLock) {
                    out.writeObject(message);
                    out.reset();
                    out.flush();
                }
            } catch (IOException e) {
                System.err.println("[SOCKET] Disconnection detected on write for: " + getNickname());
                handleClientDisconnection();
            }
        }
    }

    @Override
    public void fullSync(BoardDTO board, List<PlayerDTO> players, String activePlayer, List<AvailableActionDTO> actions) {
        sendMessage(new FullSyncMessage(board, players, activePlayer, actions));
    }

    @Override
    public void deltaEvent(List<GameEventDTO> events, List<AvailableActionDTO> nextActions, String activePlayer) {
        sendMessage(new DeltaEventMessage(events, nextActions, activePlayer));
    }

    @Override
    public void error(String error) {
        sendMessage(new ErrorMessage(error));
    }

    @Override
    public void matchmakingSuccess(String text) {
        sendMessage(new MatchmakingSuccessMessage(text));
    }

    @Override
    public void availableGames(List<GameInfoDTO> games) {
        sendMessage(new AvailableGamesResponseMessage(games));
    }

    @Override
    public void gameAborted(String reason) {
        sendMessage(new GameAbortedMessage(reason));
    }

    @Override
    public void roomUpdate(String notification, List<String> currentPlayers) {
        sendMessage(new RoomUpdateMessage(notification, currentPlayers));
    }

    @Override
    public void gameLeftSuccess(String text) {
        sendMessage(new GameLeftSuccessMessage(text));
    }

    /** Main loop: receives client messages and routes them to matchmaking or game logic. */
    @Override
    public void run() {
        try {
            while (active.get()) {
                //TODO riscrivere con visitor per questo ed RMICLIENTHANDLER
                Object input = in.readObject();
                if (input instanceof PingMessage) {
                    sendMessage(new PongMessage());
                    continue;
                }
                if (input instanceof DisconnectionMessage ds) {
                    handleClientDisconnection();
                }
                else if (input instanceof MatchmakingMessage mm) {
                    MatchmakingState currentMatchmaking = this.matchmakingState;
                    if (currentMatchmaking != null) {
                        mm.accept(currentMatchmaking);
                    } else {
                        error("Already in game. Cannot send matchmaking messages.");
                    }
                } else if (input instanceof InGameMessage igm) {
                    VirtualView currentView = this.virtualView;
                    if (currentView != null) {
                        igm.accept(currentView);
                    } else {
                        error("Not in a game yet.");
                    }
                } else {
                    error("Unknown message type.");
                }
            }
        } catch (SocketTimeoutException e) {
            System.err.println("[SOCKET] Timeout: Il client " + nickname + " non invia ping. Cavo staccato o freeze.");
        } catch (EOFException e) {
            System.out.println("[SOCKET] Il client " + nickname + " ha chiuso la connessione in modo pulito (senza messaggio di disconnessione).");
        } catch (SocketException e) {
            System.err.println("[SOCKET] Connessione interrotta bruscamente per " + nickname + " (possibile Alt+F4 o crash). Dettaglio: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("[SOCKET] Ricevuto oggetto sconosciuto da " + nickname);
        } catch (IOException e) {
            System.err.println("[SOCKET] Errore generico di I/O per " + nickname + ": " + e.getMessage());
        } finally {
            handleClientDisconnection();
        }
    }

    @Override
    public void returnToLobby(int playerCount) {
        synchronized (this) {
            if (this.nickname != null) {
                gameManager.unregisterNickname(this.nickname);
                this.nickname = null;
            }
            this.virtualView = null;
            this.lastMatchPlayerCount = playerCount;
            this.matchmakingState = new MatchmakingState(this, gameManager);
        }
    }

    /** Handles client disconnection, notifying game logic or cleaning matchmaking state. */
    private void handleClientDisconnection() {
        if (!active.compareAndSet(true, false)) return;

        closeConnection();
        VirtualView currentView = this.virtualView;
        String currentNickname = this.nickname;

        if (currentView != null) {
            currentView.handleDisconnection();
        } else if (currentNickname != null) {
            GameRoom room = gameManager.getGameRoomByPlayer(currentNickname);
            if (room != null) {
                try {
                    room.removePlayer(currentNickname);
                } catch (IllegalStateException e) {
                    System.out.println("[RMI] Disconnessione tardiva in lobby per: " + currentNickname);
                }
            }
            else{
                gameManager.unregisterNickname(currentNickname);
            }
        }
    }

    /** Closes socket and associated streams. */
    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }
}
