package it.polimi.ingsw.server.network.handler;

import it.polimi.ingsw.common.message.DisconnectionMessage;
import it.polimi.ingsw.common.message.client.ClientMessage;
import it.polimi.ingsw.common.message.client.PingMessage;
import it.polimi.ingsw.common.message.server.AvailableGamesResponseMessage;
import it.polimi.ingsw.common.message.server.DeltaEventMessage;
import it.polimi.ingsw.common.message.server.ErrorMessage;
import it.polimi.ingsw.common.message.server.FullSyncMessage;
import it.polimi.ingsw.common.message.server.GameAbortedMessage;
import it.polimi.ingsw.common.message.server.GameCompletedMessage;
import it.polimi.ingsw.common.message.server.GameLeftSuccessMessage;
import it.polimi.ingsw.common.message.server.LeaderboardResponseMessage;
import it.polimi.ingsw.common.message.server.MatchmakingSuccessMessage;
import it.polimi.ingsw.common.message.server.PongMessage;
import it.polimi.ingsw.common.message.server.RoomUpdateMessage;
import it.polimi.ingsw.common.message.server.ServerMessage;
import it.polimi.ingsw.common.network.dto.BoardDto;
import it.polimi.ingsw.common.network.dto.GameInfoDto;
import it.polimi.ingsw.common.network.dto.LeaderboardSnapshotDto;
import it.polimi.ingsw.common.network.dto.PlayerDto;
import it.polimi.ingsw.common.network.dto.PlayerGameCompletedDto;
import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.common.network.dto.event.GameEventDto;
import it.polimi.ingsw.server.controller.LobbyController;
import it.polimi.ingsw.server.lobby.GameManagerInterface;
import it.polimi.ingsw.server.network.ClientProxy;
import it.polimi.ingsw.server.network.ConnectionSession;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Socket-based client handler that manages socket I/O and delegates connection lifecycle to ConnectionSession. */
public class SocketClientHandler implements ClientProxy, Runnable {

    private static final Logger LOGGER = Logger.getLogger(SocketClientHandler.class.getName());

    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final ConnectionSession session;
    private final Object streamLock = new Object();

    /** Constructs the handler and initializes I/O streams. @param socket client socket @param gameManager game manager instance */
    public SocketClientHandler(Socket socket, GameManagerInterface gameManager, LobbyController lobbyController) throws IOException {
        this.socket = socket;
        this.socket.setSoTimeout(10000);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.session = new ConnectionSession(
                this,
                gameManager,
                lobbyController,
                this::closeConnection,
                LOGGER,
                "SOCKET"
        );
    }

    private void sendMessage(ServerMessage message) {
        if (!session.isActive()) {
            return;
        }
        try {
            synchronized (streamLock) {
                out.writeObject(message);
                out.reset();
                out.flush();
            }
        } catch (IOException e) {
            LOGGER.log(Level.INFO, () -> "[SOCKET] Disconnection detected on write for "
                    + session.getNickname() + ": " + e.getMessage());
            session.handleClientDisconnection();
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "[SOCKET] Unexpected failure while sending message to "
                    + session.getNickname(), e);
            session.handleClientDisconnection();
        }
    }

    @Override
    public void fullSync(BoardDto board, List<PlayerDto> players, String activePlayer, List<ActionDto> actions) {
        sendMessage(new FullSyncMessage(board, players, activePlayer, actions));
    }

    @Override
    public void deltaEvent(List<GameEventDto> events, List<ActionDto> nextActions, String activePlayer) {
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
    public void availableGames(List<GameInfoDto> games) {
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
    public void gameCompleted(PlayerGameCompletedDto completedGame) {
        sendMessage(new GameCompletedMessage(completedGame));
    }

    @Override
    public void leaderboard(LeaderboardSnapshotDto leaderboard) {
        sendMessage(new LeaderboardResponseMessage(leaderboard));
    }

    /** Main loop: receives client messages and routes them to matchmaking or game logic. */
    @Override
    public void run() {
        try {
            while (session.isActive()) {
                Object input = in.readObject();
                if (input instanceof PingMessage) {
                    sendMessage(new PongMessage());
                    continue;
                }
                if (input instanceof DisconnectionMessage) {
                    session.handleClientDisconnection();
                    continue;
                }
                if (input instanceof ClientMessage message) {
                    message.dispatchTo(session.currentState());
                } else {
                    error("Unknown message type.");
                }
            }
        } catch (SocketTimeoutException e) {
            LOGGER.log(Level.INFO, () -> "[SOCKET] Timeout for " + session.getNickname()
                    + ": no ping received. Detail: " + e.getMessage());
        } catch (EOFException e) {
            LOGGER.log(Level.INFO, () -> "[SOCKET] Client " + session.getNickname()
                    + " closed the connection without a disconnection message. Detail: " + e.getMessage());
        } catch (SocketException e) {
            LOGGER.log(Level.INFO, () -> "[SOCKET] Connection interrupted for " + session.getNickname()
                    + ". Detail: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, "[SOCKET] Received an unknown object from " + session.getNickname(), e);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "[SOCKET] I/O error for " + session.getNickname(), e);
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "[SOCKET] Unexpected failure while handling client "
                    + session.getNickname(), e);
        } finally {
            session.handleClientDisconnection();
        }
    }

    /** Closes socket and associated streams. */
    private void closeConnection() {
        closeResource(in, "input stream");
        closeResource(out, "output stream");
        closeResource(socket, "socket");
    }

    private void closeResource(Closeable resource, String description) {
        try {
            resource.close();
        } catch (IOException e) {
            LOGGER.log(Level.FINE, "[SOCKET] Error while closing " + description + " for "
                    + session.getNickname(), e);
        }
    }
}
