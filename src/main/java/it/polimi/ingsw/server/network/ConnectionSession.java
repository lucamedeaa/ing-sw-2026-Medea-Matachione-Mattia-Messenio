package it.polimi.ingsw.server.network;

import it.polimi.ingsw.common.network.dto.*;
import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.common.network.dto.event.GameEventDto;
import it.polimi.ingsw.server.controller.GameController;
import it.polimi.ingsw.server.controller.LobbyController;
import it.polimi.ingsw.server.leaderboard.LeaderboardService;
import it.polimi.ingsw.server.lobby.GameManagerInterface;
import it.polimi.ingsw.server.network.state.ConnectionState;
import it.polimi.ingsw.server.network.state.InGameConnectionState;
import it.polimi.ingsw.server.network.state.LobbyConnectionState;
import it.polimi.ingsw.server.network.state.PostGameConnectionState;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Shared connection lifecycle and state management used by every server transport. */
public class ConnectionSession implements ConnectionContext, ConnectionState {

    private final ClientProxy client;
    private final GameManagerInterface gameManager;
    private final ConnectionState lobbyState;
    private final Logger logger;
    private final String logPrefix;
    private final ExecutorService sessionExecutor;
    private final ExecutorService outboundExecutor;

    private volatile Thread sessionThread;
    private ConnectionState connectionState;
    private volatile String nickname;
    private boolean disconnected;

    /**
     * Creates the shared session object for a transport handler.
     *
     * @param client transport-specific client proxy
     * @param gameManager game manager used for nickname cleanup
     * @param lobbyController lobby controller used by the lobby state
     * @param logger logger used for connection failures
     * @param logPrefix prefix used in log messages and worker thread names
     */
    public ConnectionSession(
            ClientProxy client,
            GameManagerInterface gameManager,
            LobbyController lobbyController,
            Logger logger,
            String logPrefix
    ) {
        this.client = Objects.requireNonNull(client);
        this.gameManager = Objects.requireNonNull(gameManager);
        this.logger = Objects.requireNonNull(logger);
        this.logPrefix = Objects.requireNonNull(logPrefix);
        this.sessionExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, logPrefix + "-session");
            thread.setDaemon(true);
            this.sessionThread = thread;
            return thread;
        });
        this.outboundExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, logPrefix + "-outbound");
            thread.setDaemon(true);
            return thread;
        });
        this.lobbyState = new LobbyConnectionState(this, Objects.requireNonNull(lobbyController));
        this.connectionState = lobbyState;
    }

    /** {@inheritDoc} */
    @Override
    public void createGame(String nickname, int maxPlayers) {
        executeOnSession(() -> connectionState.createGame(nickname, maxPlayers));
    }

    /** {@inheritDoc} */
    @Override
    public void joinGame(String nickname, String gameId) {
        executeOnSession(() -> connectionState.joinGame(nickname, gameId));
    }

    /** {@inheritDoc} */
    @Override
    public void getAvailableGames() {
        executeOnSession(() -> connectionState.getAvailableGames());
    }

    /** {@inheritDoc} */
    @Override
    public void leaveGame() {
        executeOnSession(() -> connectionState.leaveGame());
    }

    /** {@inheritDoc} */
    @Override
    public void placeTotem(int positionIndex) {
        executeOnSession(() -> connectionState.placeTotem(positionIndex));
    }

    /** {@inheritDoc} */
    @Override
    public void takeCard(int row, int col) {
        executeOnSession(() -> connectionState.takeCard(row, col));
    }

    /** {@inheritDoc} */
    @Override
    public void skipAction() {
        executeOnSession(() -> connectionState.skipAction());
    }

    /** {@inheritDoc} */
    @Override
    public void getLeaderboard() {
        executeOnSession(() -> connectionState.getLeaderboard());
    }

    /** {@inheritDoc} */
    @Override
    public void handleDisconnection() {
        handleClientDisconnection();
    }

    /** {@inheritDoc} */
    @Override
    public void setNickname(String nickname) {
        executeOnSession(() -> this.nickname = nickname);
    }

    /** {@inheritDoc} */
    @Override
    public String getNickname() {
        return this.nickname;
    }

    /** {@inheritDoc} */
    @Override
    public void transitionToGameState(GameController gameController) {
        executeOnSession(() -> this.connectionState = new InGameConnectionState(this.nickname, this, gameController));
    }

    /** {@inheritDoc} */
    @Override
    public void transitionToAfterGameState(int playerCount, LeaderboardService leaderboardService) {
        executeOnSession(() -> this.connectionState = new PostGameConnectionState(this, playerCount, leaderboardService));
    }

    /** {@inheritDoc} */
    @Override
    public void transitionToLobby() {
        executeOnSession(() -> {
            if (this.nickname != null) {
                gameManager.unregisterNickname(this.nickname);
                this.nickname = null;
            }
            this.connectionState = lobbyState;
        });
    }

    /** {@inheritDoc} */
    @Override
    public void clearNickname() {
        executeOnSession(() -> {
            if (this.nickname != null) {
                gameManager.unregisterNickname(this.nickname);
                this.nickname = null;
            }
        });
    }

    /**
     * Marks the session logically disconnected and lets the current state clean up.
     */
    public void handleClientDisconnection() {
        executeOnSession(this::cleanupDisconnection);
    }

    private void executeOnSession(Runnable operation) {
        if (Thread.currentThread() == sessionThread) {
            if (!disconnected) {
                operation.run();
            }
            return;
        }

        try {
            sessionExecutor.execute(() -> {
                if (disconnected) {
                    return;
                }
                try {
                    operation.run();
                } catch (RuntimeException e) {
                    logger.log(Level.SEVERE, "[" + logPrefix + "] Unexpected failure while handling session task for "
                            + getNickname(), e);
                    cleanupDisconnection();
                }
            });
        } catch (RejectedExecutionException e) {
            logger.log(Level.FINE, "[" + logPrefix + "] Dropped session task for closed connection "
                    + getNickname(), e);
        }
    }

    private void cleanupDisconnection() {
        if (disconnected) {
            return;
        }
        outboundExecutor.shutdownNow();

        try {
            connectionState.handleDisconnection();
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "[" + logPrefix + "] Disconnection cleanup failed for "
                    + nickname, e);
        } finally {
            disconnected = true;
            sessionExecutor.shutdown();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void fullSync(BoardDto board, List<PlayerDto> players, String activePlayer, List<ActionDto> actions, InitTurnOrderTileDto turnOrderTile) {
        enqueueOutbound(() -> client.fullSync(board, players, activePlayer, actions, turnOrderTile));
    }

    /** {@inheritDoc} */
    @Override
    public void deltaEvent(List<GameEventDto> events, List<ActionDto> nextActions, String activePlayer) {
        enqueueOutbound(() -> client.deltaEvent(events, nextActions, activePlayer));
    }

    /** {@inheritDoc} */
    @Override
    public void error(String error) {
        enqueueOutbound(() -> client.error(error));
    }

    /** {@inheritDoc} */
    @Override
    public void matchmakingSuccess(String text) {
        enqueueOutbound(() -> client.matchmakingSuccess(text));
    }

    /** {@inheritDoc} */
    @Override
    public void availableGames(List<GameInfoDto> games) {
        enqueueOutbound(() -> client.availableGames(games));
    }

    /** {@inheritDoc} */
    @Override
    public void gameAborted(String reason) {
        enqueueOutbound(() -> client.gameAborted(reason));
    }

    /** {@inheritDoc} */
    @Override
    public void roomUpdate(String notification, List<String> currentPlayers) {
        enqueueOutbound(() -> client.roomUpdate(notification, currentPlayers));
    }

    /** {@inheritDoc} */
    @Override
    public void gameLeftSuccess(String text) {
        enqueueOutbound(() -> client.gameLeftSuccess(text));
    }

    /** {@inheritDoc} */
    @Override
    public void gameCompleted(PlayerGameCompletedDto completedGame) {
        enqueueOutbound(() -> client.gameCompleted(completedGame));
    }

    /** {@inheritDoc} */
    @Override
    public void leaderboard(LeaderboardSnapshotDto leaderboard) {
        enqueueOutbound(() -> client.leaderboard(leaderboard));
    }

    private void enqueueOutbound(Runnable delivery) {
        try {
            outboundExecutor.submit(() -> {
                try {
                    delivery.run();
                } catch (RuntimeException e) {
                    logger.log(Level.SEVERE, "[" + logPrefix + "] Unexpected failure while delivering message to "
                            + getNickname(), e);
                    handleClientDisconnection();
                }
            });
        } catch (RejectedExecutionException e) {
            logger.log(Level.FINE, "[" + logPrefix + "] Dropped outbound message for closed connection "
                    + getNickname(), e);
        }
    }
}
