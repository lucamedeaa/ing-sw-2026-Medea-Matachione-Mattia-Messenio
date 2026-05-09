package it.polimi.ingsw.server.network.handler;

import it.polimi.ingsw.common.network.dto.BoardDto;
import it.polimi.ingsw.common.network.dto.GameInfoDto;
import it.polimi.ingsw.common.network.dto.LeaderboardSnapshotDto;
import it.polimi.ingsw.common.network.dto.PlayerDto;
import it.polimi.ingsw.common.network.dto.PlayerGameCompletedDto;
import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.common.network.dto.event.GameEventDto;
import it.polimi.ingsw.common.rmi.RMIClientCallback;
import it.polimi.ingsw.common.rmi.RMIServerSession;
import it.polimi.ingsw.server.controller.LobbyController;
import it.polimi.ingsw.server.lobby.GameManagerInterface;
import it.polimi.ingsw.server.network.ClientProxy;
import it.polimi.ingsw.server.network.ConnectionSession;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/** RMI client handler exposing native RPC methods for client actions. */
public class RMmiClientHandler extends UnicastRemoteObject implements ClientProxy, RMIServerSession {

    private static final Logger LOGGER = Logger.getLogger(RMmiClientHandler.class.getName());

    private final RMIClientCallback callback;
    private final ConnectionSession session;
    private final ScheduledExecutorService timeoutChecker;
    private final AtomicLong lastPingTime = new AtomicLong();

    public RMmiClientHandler(
            GameManagerInterface gameManager,
            LobbyController lobbyController,
            RMIClientCallback callback
    ) throws RemoteException {
        super();
        this.callback = callback;
        this.lastPingTime.set(System.currentTimeMillis());
        this.timeoutChecker = Executors.newSingleThreadScheduledExecutor();
        this.session = new ConnectionSession(
                this,
                gameManager,
                lobbyController,
                this::closeConnection,
                LOGGER,
                "RMI"
        );

        this.timeoutChecker.scheduleAtFixedRate(() -> {
            if (session.isActive() && (System.currentTimeMillis() - lastPingTime.get() > 10000)) {
                LOGGER.warning("[RMI] Timeout: client " + session.getNickname()
                        + " did not send ping. Marked as disconnected.");
                session.handleClientDisconnection();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public void ping() {
        handleClientAction("ping", this::touch);
    }

    @Override
    public void disconnect() {
        handleClientAction("disconnect", () -> {
            touch();
            session.handleClientDisconnection();
        });
    }

    @Override
    public void createGame(String nickname, int maxPlayers) {
        handleClientAction("create game", () -> session.currentState().createGame(nickname, maxPlayers));
    }

    @Override
    public void joinGame(String nickname, String gameId) {
        handleClientAction("join game", () -> session.currentState().joinGame(nickname, gameId));
    }

    @Override
    public void getAvailableGames() {
        handleClientAction("get available games", () -> session.currentState().getAvailableGames());
    }

    @Override
    public void leaveGame() {
        handleClientAction("leave game", () -> session.currentState().leaveGame());
    }

    @Override
    public void placeTotem(int positionIndex) {
        handleClientAction("place totem", () -> session.currentState().placeTotem(positionIndex));
    }

    @Override
    public void takeCard(int row, int col) {
        handleClientAction("take card", () -> session.currentState().takeCard(row, col));
    }

    @Override
    public void skipAction() {
        handleClientAction("skip action", () -> session.currentState().skipAction());
    }

    @Override
    public void getLeaderboard() {
        handleClientAction("get leaderboard", () -> session.currentState().getLeaderboard());
    }

    @Override
    public void fullSync(BoardDto board, List<PlayerDto> players, String activePlayer, List<ActionDto> actions) {
        deliver(() -> callback.onFullSync(board, players, activePlayer, actions));
    }

    @Override
    public void deltaEvent(List<GameEventDto> events, List<ActionDto> nextActions, String activePlayer) {
        deliver(() -> callback.onDeltaEvent(events, nextActions, activePlayer));
    }

    @Override
    public void error(String error) {
        deliver(() -> callback.onError(error));
    }

    @Override
    public void matchmakingSuccess(String text) {
        deliver(() -> callback.onMatchmakingSuccess(text));
    }

    @Override
    public void availableGames(List<GameInfoDto> games) {
        deliver(() -> callback.onAvailableGames(games));
    }

    @Override
    public void gameAborted(String reason) {
        deliver(() -> callback.onGameAborted(reason));
    }

    @Override
    public void roomUpdate(String notification, List<String> currentPlayers) {
        deliver(() -> callback.onRoomUpdate(notification, currentPlayers));
    }

    @Override
    public void gameLeftSuccess(String text) {
        deliver(() -> callback.onGameLeftSuccess(text));
    }

    @Override
    public void gameCompleted(PlayerGameCompletedDto completedGame) {
        deliver(() -> callback.onGameCompleted(completedGame));
    }

    @Override
    public void leaderboard(LeaderboardSnapshotDto leaderboard) {
        deliver(() -> callback.onLeaderboard(leaderboard));
    }

    private void touch() {
        this.lastPingTime.set(System.currentTimeMillis());
    }

    private void handleClientAction(String actionName, Runnable action) {
        try {
            touch();
            action.run();
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "[RMI] Unexpected failure while handling " + actionName
                    + " for " + session.getNickname(), e);
            disconnectAfterUnexpectedFailure();
        }
    }

    private void disconnectAfterUnexpectedFailure() {
        try {
            session.handleClientDisconnection();
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "[RMI] Failed to disconnect " + session.getNickname()
                    + " after unexpected failure", e);
        }
    }

    private void deliver(RemoteCall call) {
        if (!session.isActive()) {
            return;
        }
        try {
            call.run();
        } catch (RemoteException e) {
            LOGGER.log(Level.INFO, () -> "[RMI] Disconnection detected on write for "
                    + session.getNickname() + ": " + e.getMessage());
            session.handleClientDisconnection();
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "[RMI] Unexpected failure while delivering callback to "
                    + session.getNickname(), e);
            disconnectAfterUnexpectedFailure();
        }
    }

    private void closeConnection() {
        timeoutChecker.shutdownNow();
        try {
            UnicastRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException e) {
            LOGGER.log(Level.FINE, "[RMI] Handler already unexported for " + session.getNickname(), e);
        }
    }

    @FunctionalInterface
    private interface RemoteCall {
        void run() throws RemoteException;
    }
}
