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
import it.polimi.ingsw.server.leaderboard.LeaderboardService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/** RMI client handler exposing native RPC methods for client actions. */
public class RMIClientHandler extends UnicastRemoteObject implements ConnectionContext, RMIServerSession {

    private final GameManagerInterface gameManager;
    private final RMIClientCallback callback;
    private volatile ConnectionState connectionState;
    // Lock order: lifecycleLock -> GameRoom room lock. Do not perform RMI callbacks while holding it.
    private final Object lifecycleLock = new Object();
    private String nickname;

    private final ScheduledExecutorService timeoutChecker;

    private final AtomicBoolean active = new AtomicBoolean(true);
    private final AtomicLong lastPingTime = new AtomicLong();

    public RMIClientHandler(GameManagerInterface gameManager, RMIClientCallback callback) throws RemoteException {
        super();
        this.callback = callback;
        this.gameManager = gameManager;
        this.connectionState = createLobbyState();
        this.lastPingTime.set(System.currentTimeMillis());
        this.timeoutChecker = Executors.newSingleThreadScheduledExecutor();

        this.timeoutChecker.scheduleAtFixedRate(() -> {
            if (active.get() && (System.currentTimeMillis() - lastPingTime.get() > 10000)) {
                System.err.println("[RMI] Timeout: Il client " + getNickname() + " non invia ping. Ritenuto morto.");
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
        touch();
    }

    @Override
    public void disconnect() {
        touch();
        handleClientDisconnection();
    }

    @Override
    public void createGame(String nickname, int maxPlayers) {
        currentState().createGame(nickname, maxPlayers);
    }

    @Override
    public void joinGame(String nickname, String gameId) {
        currentState().joinGame(nickname, gameId);
    }

    @Override
    public void getAvailableGames() {
        currentState().getAvailableGames();
    }

    @Override
    public void leaveGame() {
        currentState().leaveGame();
    }

    @Override
    public void placeTotem(int positionIndex) {
        currentState().placeTotem(positionIndex);
    }

    @Override
    public void takeCard(int row, int col) {
        currentState().takeCard(row, col);
    }

    @Override
    public void skipAction() {
        currentState().skipAction();
    }

    @Override
    public void getLeaderboard() {
        currentState().getLeaderboard();
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
            this.connectionState = createLobbyState();
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

    private void deliver(RemoteCall call) {
        if (!active.get()) {
            return;
        }
        try {
            call.run();
        } catch (RemoteException e) {
            System.err.println("[RMI] Disconnection detected on write for: " + getNickname());
            handleClientDisconnection();
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
        stateToNotify.handleDisconnection();
    }

    private ConnectionState createLobbyState() {
        return new LobbyConnectionState(this, new LobbyController(gameManager));
    }

    @Override
    public <T> T withConnectionLock(LockedConnectionOperation<T> operation) throws Exception {
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
        } catch (java.rmi.NoSuchObjectException e) {
            System.err.println("[RMI] Impossibile eseguire l'unexport dell'oggetto: " + e.getMessage());
        }
    }

    @FunctionalInterface
    private interface RemoteCall {
        void run() throws RemoteException;
    }
}
