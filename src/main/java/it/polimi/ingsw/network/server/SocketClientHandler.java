package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.server.GameManagerInterface;
import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.LeaderboardSnapshot;
import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.dto.PlayerDTO;
import it.polimi.ingsw.network.dto.PlayerGameCompletedDTO;
import it.polimi.ingsw.server.leaderboard.LeaderboardService;

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
public class SocketClientHandler implements ConnectionContext, Runnable {

    private final Socket socket;
    private final GameManagerInterface gameManager;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private AtomicBoolean active = new AtomicBoolean(true);
    private final Object streamLock = new Object();
    private String nickname;

    private volatile ConnectionState connectionState;

    /** Constructs the handler and initializes I/O streams. @param socket client socket @param gameManager game manager instance */
    public SocketClientHandler(Socket socket, GameManagerInterface gameManager) throws IOException {
        this.socket = socket;
        this.gameManager = gameManager;
        this.connectionState = createLobbyState();

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

    @Override
    public boolean isActive() {
        return this.active.get();
    }


    @Override
    public void transitionToGameState(GameController gameController) {
        this.connectionState = new InGameConnectionState(this.nickname, this, gameController);
        if (!this.active.get()) {
            this.connectionState.handleDisconnection();
        }
    }

    @Override
    public void transitionToAfterGameState(int playerCount, LeaderboardService leaderboardService) {
        this.connectionState = new AfterGameConnectionState(this, playerCount, leaderboardService);
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

    @Override
    public void gameCompleted(PlayerGameCompletedDTO completedGame) {
        sendMessage(new GameCompletedMessage(completedGame));
    }

    @Override
    public void leaderboard(LeaderboardSnapshot leaderboard) {
        sendMessage(new LeaderboardResponseMessage(leaderboard));
    }

    /** Main loop: receives client messages and routes them to matchmaking or game logic. */
    @Override
    public void run() {
        try {
            while (active.get()) {
                Object input = in.readObject();
                if (input instanceof PingMessage) {
                    sendMessage(new PongMessage());
                    continue;
                }
                if (input instanceof DisconnectionMessage) {
                    handleClientDisconnection();
                    continue;
                }
                if (input instanceof ClientMessage message) {
                    message.dispatchTo(this.connectionState);
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
    public void transitionToLobby() {
        synchronized (this) {
            if (this.nickname != null) {
                gameManager.unregisterNickname(this.nickname);
                this.nickname = null;
            }
            this.connectionState = createLobbyState();
        }
    }

    @Override
    public void clearNickname() {
        synchronized (this) {
            if (this.nickname != null) {
                gameManager.unregisterNickname(this.nickname);
                this.nickname = null;
            }
        }
    }

    /** Handles client disconnection, notifying game logic or cleaning matchmaking state. */
    private void handleClientDisconnection() {
        if (!active.compareAndSet(true, false)) return;

        closeConnection();
        // Use the connection lock to serialize cleanup with lobby admission.
        synchronized (this) {
            this.connectionState.handleDisconnection();
        }
    }

    private ConnectionState createLobbyState() {
        return new LobbyConnectionState(this, new LobbyController(gameManager));
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
