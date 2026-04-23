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

    /** Constructs the handler and initializes I/O streams. @param socket client socket @param gameManager game manager instance */
    public SocketClientHandler(Socket socket, GameManager gameManager) {
        this.socket = socket;
        this.gameManager = gameManager;
        this.active = true;
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            this.active = false;
        }
    }

    /** Associates a VirtualView to forward in-game messages. @param virtualView virtual view */
    @Override
    public void setVirtualView(VirtualView virtualView) {
        this.virtualView = virtualView;
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

                if (input instanceof ClientMessage message) {
                    if (virtualView != null) {
                        virtualView.onMessageReceived(message);
                    } else {
                        handleMatchmakingMessage(message);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[SOCKET] Disconnection detected on read for: " + nickname);
            handleDisconnection();
        } finally {
            closeConnection();
        }
    }

    /** Handles messages related to matchmaking (before joining a game). @param message client message */
    private void handleMatchmakingMessage(ClientMessage message) {
        try {
            if (message instanceof GetAvailableGamesMessage) {
                var availableGames = gameManager.getAvailableGames();
                send(new AvailableGamesResponseMessage(availableGames));

            } else if (message instanceof CreateGameMessage createMsg) {
                this.nickname = createMsg.getNickname();
                String gameId = gameManager.createNewGame(this.nickname, createMsg.getMaxPlayers());
                GameRoom room = gameManager.getGame(gameId);

                room.addPlayer(this.nickname, this);
                send(new MatchmakingSuccessMessage("Game created. Waiting for other players..."));

            } else if (message instanceof JoinGameMessage joinMsg) {
                this.nickname = joinMsg.getNickname();
                GameRoom room = gameManager.getGame(joinMsg.getGameId());

                if (room == null) {
                    send(new ErrorMessageDTO("Requested game does not exist."));
                    return;
                }

                room.addPlayer(this.nickname, this);
                send(new MatchmakingSuccessMessage("Joined game successfully. Waiting to start..."));

            } else {
                send(new ErrorMessageDTO("Error: you are not in a game yet."));
            }
        } catch (Exception e) {
            send(new ErrorMessageDTO("Error during access: " + e.getMessage()));
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