package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.server.GameManager;
import it.polimi.ingsw.server.GameRoom;
import it.polimi.ingsw.view.VirtualView;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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

    /** Sends a server message to the client; handles disconnection on failure. @param message message to send */
    @Override
    public void send(ServerMessage message) {
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

    /** Main loop: receives client messages and routes them to matchmaking or game logic. */
    @Override
    public void run() {
        try {
            while (active.get()) {
                //TODO riscrivere con visitor per questo ed RMICLIENTHANDLER
                Object input = in.readObject();
                if (input instanceof PingMessage) {
                    send(new PongMessage());
                    continue;
                }
                if (input instanceof DisconnectionMessage ds) {
                    handleClientDisconnection();
                }
                else if (input instanceof MatchmakingMessage mm) {
                    if (matchmakingState != null) {
                        mm.accept(matchmakingState);
                    } else {
                        send(new ErrorMessageDTO("Already in game. Cannot send matchmaking messages."));
                    }
                } else if (input instanceof InGameMessage igm) {
                    if (virtualView != null) {
                        igm.accept(virtualView);
                    } else {
                        send(new ErrorMessageDTO("Not in a game yet."));
                    }
                } else {
                    send(new ErrorMessageDTO("Unknown message type."));
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

    /** Handles client disconnection, notifying game logic or cleaning matchmaking state. */
    private void handleClientDisconnection() {
        if (!active.compareAndSet(true, false)) return;

        closeConnection();

        if (virtualView != null) {
            virtualView.handleDisconnection();
        } else if (nickname != null) {
            GameRoom room = gameManager.getGameRoomByPlayer(nickname);
            if (room != null) {
                try {
                    room.removePlayer(nickname);
                } catch (IllegalStateException e) {
                    System.out.println("[RMI] Disconnessione tardiva in lobby per: " + nickname);
                }
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