package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.model.EventApplier;
import it.polimi.ingsw.client.model.LobbyModel;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.listeners.GameEndedView;
import it.polimi.ingsw.client.view.listeners.InGameView;
import it.polimi.ingsw.client.view.listeners.LobbyView;
import it.polimi.ingsw.client.view.listeners.MatchmakingView;
import it.polimi.ingsw.common.network.dto.*;
import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.common.network.dto.event.GameEventDto;
import it.polimi.ingsw.server.model.update.InitTurnOrderTileUpdate;

import java.util.List;

/**
 * Coordina le notifiche del server, i modelli del client e le view attive.
 * Grazie al DispatchingNotificationReceiver, tutti i metodi qui sotto sono eseguiti
 * nel thread della UI (uiExecutor per TUI o JavaFX Thread per GUI).
 */
public class ClientNotificationController implements ServerNotificationReceiver {
    private final LobbyModel lobbyModel;
    private final GameModel gameModel;
    private final EventApplier applier;

    private MatchmakingView matchmakingView;
    private LobbyView lobbyView;
    private InGameView inGameView;
    private GameEndedView gameEndedView;

    /**
     * Creates a new {@code ClientNotificationController} instance.
     *
     * @param lobbyModel lobby model
     * @param gameModel game model
     * @param applier event applier
     */
    public ClientNotificationController(LobbyModel lobbyModel, GameModel gameModel, EventApplier applier) {
        this.lobbyModel = lobbyModel;
        this.gameModel = gameModel;
        this.applier = applier;
    }

    /**
     * Sets the matchmaking view.
     *
     * @param v matchmaking view instance
     */
    public void setMatchmakingView(MatchmakingView v) { this.matchmakingView = v; }
    /**
     * Sets the lobby view.
     *
     * @param v lobby view instance
     */
    public void setLobbyView(LobbyView v) { this.lobbyView = v; }
    /**
     * Sets the in game view.
     *
     * @param v in-game view instance
     */
    public void setInGameView(InGameView v) { this.inGameView = v; }
    /**
     * Sets the game ended view.
     *
     * @param v game-ended view instance
     */
    public void setGameEndedView(GameEndedView v) { this.gameEndedView = v; }

    /** {@inheritDoc} */
    @Override
    public void availableGames(List<GameInfoDto> games) {
        lobbyModel.setAvailableGames(games);
        if (matchmakingView != null) matchmakingView.onAvailableGames(games);
    }

    /** {@inheritDoc} */
    @Override
    public void matchmakingSuccess(String text) {
        if (matchmakingView != null) matchmakingView.onMatchmakingSuccess(text);

        lobbyModel.executeBatch(() ->
                lobbyModel.setLobbyData(lobbyModel.getLobbyPlayers(), text)
        );
    }

    /** {@inheritDoc} */
    @Override
    public void roomUpdate(String notification, List<String> currentPlayers) {
        lobbyModel.executeBatch(() -> {
            lobbyModel.setLobbyData(currentPlayers, notification);
            if (lobbyView != null) lobbyView.onRoomUpdate(notification, currentPlayers);
        });
    }

    /** {@inheritDoc} */
    @Override
    public void fullSync(BoardDto board, List<PlayerDto> players, String activePlayer, List<ActionDto> actions, InitTurnOrderTileDto turnOrderTile) {
        if (lobbyView != null) {
            lobbyView.onGameStarted();
        }


        gameModel.executeBatch(() -> {
            gameModel.reset();
            gameModel.setFullState(board, players, activePlayer);
            gameModel.setAvailableActions(actions);
            gameModel.setInitTotemPosition(turnOrderTile);
        });
    }

    /** {@inheritDoc} */
    @Override
    public void deltaEvent(List<GameEventDto> events, List<ActionDto> nextActions, String activePlayer) {
        gameModel.executeBatch(() -> {
            for (GameEventDto event : events) {
                event.accept(applier);
            }
            gameModel.setAvailableActions(nextActions);
            gameModel.setActivePlayer(activePlayer);
        });
    }

    /** {@inheritDoc} */
    @Override
    public void error(String error) {
        if (inGameView != null) {
            gameModel.setGlobalError(error);
            inGameView.onError(error);
        } else {
            lobbyModel.setGlobalError(error);
            if (matchmakingView != null) matchmakingView.onError(error);
            if (lobbyView != null) lobbyView.onError(error);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void gameAborted(String reason) {
        gameModel.reset();
        lobbyModel.setGlobalErrorSilent(reason);

        if (inGameView != null) inGameView.onReturnToMatchmaking(reason);
        if (lobbyView != null) lobbyView.onReturnToMatchmaking(reason);
    }

    /** {@inheritDoc} */
    @Override
    public void gameLeftSuccess(String text) {
        gameModel.reset();
        lobbyModel.setGlobalErrorSilent(text);

        if (inGameView != null) inGameView.onReturnToMatchmaking(text);
        if (gameEndedView != null) gameEndedView.onReturnToMatchmaking(text);
        if (lobbyView != null) lobbyView.onReturnToMatchmaking(text);
    }

    /** {@inheritDoc} */
    @Override
    public void gameCompleted(PlayerGameCompletedDto completedGame) {
        gameModel.setGameCompleted(completedGame);
    }

    /** {@inheritDoc} */
    @Override
    public void leaderboard(LeaderboardSnapshotDto leaderboard) {
        gameModel.setGlobalLeaderboard(leaderboard);
    }

    /** {@inheritDoc} */
    @Override
    public void serverDisconnected(String reason) {
        if (matchmakingView != null) matchmakingView.onServerDisconnected(reason);
        else if (lobbyView != null) lobbyView.onServerDisconnected(reason);
        else if (inGameView != null) inGameView.onServerDisconnected(reason);
        else if (gameEndedView != null) gameEndedView.onServerDisconnected(reason);
        else {
            System.err.println("\n[CONNECTION ERROR]: " + reason);
            System.exit(1);
        }
    }
}
