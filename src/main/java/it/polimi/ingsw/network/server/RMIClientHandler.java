package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.dto.LeaderboardSnapshot;
import it.polimi.ingsw.network.dto.PlayerDTO;
import it.polimi.ingsw.network.dto.PlayerGameCompletedDTO;
import it.polimi.ingsw.network.messages.GameInfoDTO;
import it.polimi.ingsw.network.rmi.RMIClientCallback;
import it.polimi.ingsw.network.rmi.RMIServerSession;
import it.polimi.ingsw.server.GameManagerInterface;
import it.polimi.ingsw.server.exceptions.LobbyActionException;
import it.polimi.ingsw.server.leaderboard.LeaderboardService;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/** RMI client handler exposing native RPC methods for client actions. */
public class RMIClientHandler extends UnicastRemoteObject implements ConnectionContext, RMIServerSession {

    private static final Logger LOGGER = Logger.getLogger(RMIClientHandler.class.getName());

    private final GameManagerInterface gameManager;
    private final RMIClientCallback callback;
    private final ConnectionState lobbyState;
    private ConnectionState connectionState;
    // Lock order: lifecycleLock -> GameRoom room lock. Do not perform RMI callbacks while holding it.
    private final Object lifecycleLock = new Object();
    private String nickname;

    private final ScheduledExecutorService timeoutChecker;

    private final AtomicBoolean active = new AtomicBoolean(true);
    private final AtomicLong lastPingTime = new AtomicLong();

    public RMIClientHandler(GameManagerInterface gameManager, LobbyController lobbyController, RMIClientCallback callback) throws RemoteException {
        super();
        this.callback = callback;
        this.gameManager = gameManager;
        this.lobbyState = new LobbyConnectionState(this, lobbyController);
        this.connectionState = lobbyState;
        this.lastPingTime.set(System.currentTimeMillis());
        this.timeoutChecker = Executors.newSingleThreadScheduledExecutor();

        this.timeoutChecker.scheduleAtFixedRate(() -> {
            if (active.get() && (System.currentTimeMillis() - lastPingTime.get() > 10000)) {
                LOGGER.warning("[RMI] Timeout: client " + getNickname() + " did not send ping. Marked as disconnected.");
                handleClientDisconnection();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public void setNickname(String nickname) {
        synchronized (lifecycleLock) {
            this.nickname = nickname;
        }
    }

    public String getNickname() {
        synchronized (lifecycleLock) {
            return this.nickname;
        }
    }

    @Override
    public boolean isActive() {
        return this.active.get();
    }

    @Override
    public void transitionToGameState(GameController gameController) {
        ConnectionState disconnectedState = null;

        synchronized (lifecycleLock) {
            ConnectionState newState = new InGameConnectionState(this.nickname, this, gameController);
            this.connectionState = newState;
            if (!this.active.get()) {
                disconnectedState = newState;
            }
        }

        if (disconnectedState != null) {
            disconnectedState.handleDisconnection();
        }
    }

    @Override
    public void transitionToAfterGameState(int playerCount, LeaderboardService leaderboardService) {
        synchronized (lifecycleLock) {
            this.connectionState = new AfterGameConnectionState(this, playerCount, leaderboardService);
        }
    }

    @Override
    public void ping() {
        handleClientAction("ping", this::touch);
    }

    @Override
    public void disconnect() {
        handleClientAction("disconnect", () -> {
            touch();
            handleClientDisconnection();
        });
    }

    @Override
    public void createGame(String nickname, int maxPlayers) {
        handleClientAction("create game", () -> currentState().createGame(nickname, maxPlayers));
    }

    @Override
    public void joinGame(String nickname, String gameId) {
        handleClientAction("join game", () -> currentState().joinGame(nickname, gameId));
    }

    @Override
    public void getAvailableGames() {
        handleClientAction("get available games", () -> currentState().getAvailableGames());
    }

    @Override
    public void leaveGame() {
        handleClientAction("leave game", () -> currentState().leaveGame());
    }

    @Override
    public void placeTotem(int positionIndex) {
        handleClientAction("place totem", () -> currentState().placeTotem(positionIndex));
    }

    @Override
    public void takeCard(int row, int col) {
        handleClientAction("take card", () -> currentState().takeCard(row, col));
    }

    @Override
    public void skipAction() {
        handleClientAction("skip action", () -> currentState().skipAction());
    }

    @Override
    public void getLeaderboard() {
        handleClientAction("get leaderboard", () -> currentState().getLeaderboard());
    }

    @Override
    public void fullSync(BoardDTO board, List<PlayerDTO> players, String activePlayer, List<AvailableActionDTO> actions) {
        deliver(() -> callback.onFullSync(board, players, activePlayer, actions));
    }

    @Override
    public void deltaEvent(List<GameEventDTO> events, List<AvailableActionDTO> nextActions, String activePlayer) {
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
    public void availableGames(List<GameInfoDTO> games) {
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
    public void gameCompleted(PlayerGameCompletedDTO completedGame) {
        deliver(() -> callback.onGameCompleted(completedGame));
    }

    @Override
    public void leaderboard(LeaderboardSnapshot leaderboard) {
        deliver(() -> callback.onLeaderboard(leaderboard));
    }

    @Override
    public void transitionToLobby() {
        synchronized (lifecycleLock) {
            if (this.nickname != null) {
                gameManager.unregisterNickname(this.nickname);
                this.nickname = null;
            }
            this.connectionState = lobbyState;
        }
    }

    @Override
    public void clearNickname() {
        synchronized (lifecycleLock) {
            if (this.nickname != null) {
                gameManager.unregisterNickname(this.nickname);
                this.nickname = null;
            }
        }
    }

    private ConnectionState currentState() {
        touch();
        synchronized (lifecycleLock) {
            return this.connectionState;
        }
    }

    private void touch() {
        this.lastPingTime.set(System.currentTimeMillis());
    }

    private void handleClientAction(String actionName, Runnable action) {
        try {
            action.run();
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "[RMI] Unexpected failure while handling " + actionName
                    + " for " + getNickname(), e);
            disconnectAfterUnexpectedFailure();
        }
    }

    private void disconnectAfterUnexpectedFailure() {
        try {
            handleClientDisconnection();
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "[RMI] Failed to disconnect " + getNickname()
                    + " after unexpected failure", e);
        }
    }

    private void deliver(RemoteCall call) {
        if (!active.get()) {
            return;
        }
        try {
            call.run();
        } catch (RemoteException e) {
            LOGGER.log(Level.INFO, () -> "[RMI] Disconnection detected on write for "
                    + getNickname() + ": " + e.getMessage());
            handleClientDisconnection();
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "[RMI] Unexpected failure while delivering callback to " + getNickname(), e);
            disconnectAfterUnexpectedFailure();
        }
    }

    private void handleClientDisconnection() {
        ConnectionState stateToNotify;
        synchronized (lifecycleLock) {
            if (!active.compareAndSet(true, false)) {
                return;
            }
            stateToNotify = this.connectionState;
        }

        closeConnection();
        try {
            stateToNotify.handleDisconnection();
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "[RMI] Disconnection cleanup failed for " + getNickname(), e);
        }
    }

    @Override
    public <T> T withConnectionLock(LockedConnectionOperation<T> operation) throws LobbyActionException {
        synchronized (lifecycleLock) {
            return operation.run();
        }
    }

    private void closeConnection() {
        if (timeoutChecker != null && !timeoutChecker.isShutdown()) {
            timeoutChecker.shutdownNow();
        }
        try {
            UnicastRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException e) {
            LOGGER.log(Level.FINE, "[RMI] Handler already unexported for " + getNickname(), e);
        }
    }

    @FunctionalInterface
    private interface RemoteCall {
        void run() throws RemoteException;
    }
}
