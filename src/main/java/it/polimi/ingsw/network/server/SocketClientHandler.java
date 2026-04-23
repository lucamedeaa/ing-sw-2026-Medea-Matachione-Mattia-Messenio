package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.server.GameManager;
import it.polimi.ingsw.server.GameRoom;
import it.polimi.ingsw.view.VirtualView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/** Socket-based client handler that manages communication, matchmaking, and in-game message forwarding. */
public class SocketClientHandler implements ClientConnection, Runnable {

    private final Socket socket;
    private final GameManager gameManager;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private VirtualView virtualView;
    private boolean active;
    private String nickname;

    private MatchmakingState matchmakingState;

    /** Constructs the handler and initializes I/O streams. @param socket client socket @param gameManager game manager instance */
    public SocketClientHandler(Socket socket, GameManager gameManager) {
        this.socket = socket;
        this.gameManager = gameManager;
        this.active = true;
        this.matchmakingState = new MatchmakingState(this, gameManager);
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            this.active = false;
        }
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    /** Associates a VirtualView to forward in-game messages. @param virtualView virtual view */
    @Override
    public void setVirtualView(VirtualView virtualView) {
        this.virtualView = virtualView;
        // When view is set, we are no longer in matchmaking.
        this.matchmakingState = null;
    }

    /** Sends a server message to the client; handles disconnection on failure. @param message message to send */
    @Override
    public synchronized void send(ServerMessage message) {
        try {
            if (active) {
                out.writeObject(message);
                out.reset();
            }
        } catch (IOException e) {
            System.err.println("[SOCKET] Disconnection detected on write for: " + nickname);
            handleDisconnection();
        }
    }

    /** Main loop: receives client messages and routes them to matchmaking or game logic. */
    @Override
    public void run() {
        try {
            while (active) {
                Object input = in.readObject();

                if (input instanceof MatchmakingMessage mm) {
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
        } catch (Exception e) {
            System.err.println("[SOCKET] Disconnection detected on read for: " + nickname);
            handleDisconnection();
        } finally {
            closeConnection();
        }
    }

    /** Handles client disconnection, notifying game logic or cleaning matchmaking state. */
    private void handleDisconnection() {
        if (!active) return;
        this.active = false;

        closeConnection();

        if (virtualView != null) {
            virtualView.handleDisconnection(nickname);
        } else if (nickname != null) {
            GameRoom room = gameManager.getGameRoomByPlayer(nickname);
            if (room != null) {
                room.removePlayer(nickname);
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