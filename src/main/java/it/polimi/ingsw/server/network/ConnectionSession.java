package it.polimi.ingsw.server.network;

import it.polimi.ingsw.common.network.dto.BoardDto;
import it.polimi.ingsw.common.network.dto.GameInfoDto;
import it.polimi.ingsw.common.network.dto.LeaderboardSnapshotDto;
import it.polimi.ingsw.common.network.dto.PlayerDto;
import it.polimi.ingsw.common.network.dto.PlayerGameCompletedDto;
import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.common.network.dto.event.GameEventDto;
import it.polimi.ingsw.server.controller.GameController;
import it.polimi.ingsw.server.controller.LobbyController;
import it.polimi.ingsw.server.leaderboard.LeaderboardService;
import it.polimi.ingsw.server.lobby.GameManagerInterface;
import it.polimi.ingsw.server.model.exception.LobbyActionException;
import it.polimi.ingsw.server.network.state.ConnectionState;
import it.polimi.ingsw.server.network.state.InGameConnectionState;
import it.polimi.ingsw.server.network.state.LobbyConnectionState;
import it.polimi.ingsw.server.network.state.PostGameConnectionState;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Shared connection lifecycle and state management used by every server transport. */
public class ConnectionSession implements ConnectionContext {

    private final ClientProxy client;
    private final GameManagerInterface gameManager;
    private final ConnectionState lobbyState;
    private final Runnable closeConnection;
    private final Logger logger;
    private final String logPrefix;
    private final AtomicBoolean active = new AtomicBoolean(true);
    private final ExecutorService outboundExecutor;

    // Lock order: lifecycleLock -> GameRoom room lock. Do not perform client I/O while holding it.
    private final Object lifecycleLock = new Object();

    private ConnectionState connectionState;
    private String nickname;

    public ConnectionSession(
            ClientProxy client,
            GameManagerInterface gameManager,
            LobbyController lobbyController,
            Runnable closeConnection,
            Logger logger,
            String logPrefix
    ) {
        this.client = Objects.requireNonNull(client);
        this.gameManager = Objects.requireNonNull(gameManager);
        this.closeConnection = Objects.requireNonNull(closeConnection);
        this.logger = Objects.requireNonNull(logger);
        this.logPrefix = Objects.requireNonNull(logPrefix);
        this.outboundExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, logPrefix + "-outbound");
            thread.setDaemon(true);
            return thread;
        });
        this.lobbyState = new LobbyConnectionState(this, Objects.requireNonNull(lobbyController));
        this.connectionState = lobbyState;
    }

    public ConnectionState currentState() {
        synchronized (lifecycleLock) {
            return connectionState;
        }
    }

    @Override
    public void setNickname(String nickname) {
        synchronized (lifecycleLock) {
            this.nickname = nickname;
        }
    }

    @Override
    public String getNickname() {
        synchronized (lifecycleLock) {
            return this.nickname;
        }
    }

    @Override
    public boolean isActive() {
        return active.get();
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
            this.connectionState = new PostGameConnectionState(this, playerCount, leaderboardService);
        }
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

    @Override
    public <T> T withConnectionLock(LockedConnectionOperation<T> operation) throws LobbyActionException {
        synchronized (lifecycleLock) {
            return operation.run();
        }
    }

    public void handleClientDisconnection() {
        ConnectionState stateToNotify;
        String disconnectedNickname;
        synchronized (lifecycleLock) {
            if (!active.compareAndSet(true, false)) {
                return;
            }
            stateToNotify = this.connectionState;
            disconnectedNickname = this.nickname;
        }

        closeConnection.run();
        outboundExecutor.shutdownNow();
        try {
            stateToNotify.handleDisconnection();
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "[" + logPrefix + "] Disconnection cleanup failed for "
                    + disconnectedNickname, e);
        }
    }

    @Override
    public void fullSync(BoardDto board, List<PlayerDto> players, String activePlayer, List<ActionDto> actions) {
        enqueueOutbound("full sync", () -> client.fullSync(board, players, activePlayer, actions));
    }

    @Override
    public void deltaEvent(List<GameEventDto> events, List<ActionDto> nextActions, String activePlayer) {
        enqueueOutbound("delta event", () -> client.deltaEvent(events, nextActions, activePlayer));
    }

    @Override
    public void error(String error) {
        enqueueOutbound("error", () -> client.error(error));
    }

    @Override
    public void matchmakingSuccess(String text) {
        enqueueOutbound("matchmaking success", () -> client.matchmakingSuccess(text));
    }

    @Override
    public void availableGames(List<GameInfoDto> games) {
        enqueueOutbound("available games", () -> client.availableGames(games));
    }

    @Override
    public void gameAborted(String reason) {
        enqueueOutbound("game aborted", () -> client.gameAborted(reason));
    }

    @Override
    public void roomUpdate(String notification, List<String> currentPlayers) {
        enqueueOutbound("room update", () -> client.roomUpdate(notification, currentPlayers));
    }

    @Override
    public void gameLeftSuccess(String text) {
        enqueueOutbound("game left success", () -> client.gameLeftSuccess(text));
    }

    @Override
    public void gameCompleted(PlayerGameCompletedDto completedGame) {
        enqueueOutbound("game completed", () -> client.gameCompleted(completedGame));
    }

    @Override
    public void leaderboard(LeaderboardSnapshotDto leaderboard) {
        enqueueOutbound("leaderboard", () -> client.leaderboard(leaderboard));
    }

    private void enqueueOutbound(String description, Runnable delivery) {
        if (!active.get()) {
            return;
        }
        try {
            outboundExecutor.submit(() -> {
                if (!active.get()) {
                    return;
                }
                try {
                    delivery.run();
                } catch (RuntimeException e) {
                    logger.log(Level.SEVERE, "[" + logPrefix + "] Unexpected failure while delivering "
                            + description + " to " + getNickname(), e);
                    handleClientDisconnection();
                }
            });
        } catch (RejectedExecutionException e) {
            logger.log(Level.FINE, "[" + logPrefix + "] Dropped outbound " + description
                    + " for closed connection " + getNickname(), e);
        }
    }
}
