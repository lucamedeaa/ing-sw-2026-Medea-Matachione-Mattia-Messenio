package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.view.ClientEventDispatcher;
import it.polimi.ingsw.common.network.dto.*;
import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.common.network.dto.event.GameEventDto;

import java.util.List;

/** Represents the dispatching notification receiver component. */
public class DispatchingNotificationReceiver implements ServerNotificationReceiver {

    private final ClientEventDispatcher dispatcher;
    private final ServerNotificationReceiver delegate;

    /**
     * Creates a new {@code DispatchingNotificationReceiver} instance.
     *
     * @param dispatcher dispatcher
     * @param delegate delegate
     */
    public DispatchingNotificationReceiver(ClientEventDispatcher dispatcher, ServerNotificationReceiver delegate) {
        this.dispatcher = dispatcher;
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void fullSync(BoardDto board, List<PlayerDto> players, String activePlayer, List<ActionDto> actions,  InitTurnOrderTileDto turnOrderTile) {
        dispatcher.dispatch(() -> delegate.fullSync(board, players, activePlayer, actions, turnOrderTile));
    }

    /** {@inheritDoc} */
    @Override
    public void deltaEvent(List<GameEventDto> events, List<ActionDto> nextActions, String activePlayer) {
        dispatcher.dispatch(() -> delegate.deltaEvent(events, nextActions, activePlayer));
    }

    /** {@inheritDoc} */
    @Override
    public void error(String error) {
        dispatcher.dispatch(() -> delegate.error(error));
    }

    /** {@inheritDoc} */
    @Override
    public void matchmakingSuccess(String text) {
        dispatcher.dispatch(() -> delegate.matchmakingSuccess(text));
    }

    /** {@inheritDoc} */
    @Override
    public void availableGames(List<GameInfoDto> games) {
        dispatcher.dispatch(() -> delegate.availableGames(games));
    }

    /** {@inheritDoc} */
    @Override
    public void gameAborted(String reason) {
        dispatcher.dispatch(() -> delegate.gameAborted(reason));
    }

    /** {@inheritDoc} */
    @Override
    public void roomUpdate(String notification, List<String> currentPlayers) {
        dispatcher.dispatch(() -> delegate.roomUpdate(notification, currentPlayers));
    }

    /** {@inheritDoc} */
    @Override
    public void gameLeftSuccess(String text) {
        dispatcher.dispatch(() -> delegate.gameLeftSuccess(text));
    }

    /** {@inheritDoc} */
    @Override
    public void gameCompleted(PlayerGameCompletedDto completedGame) {
        dispatcher.dispatch(() -> delegate.gameCompleted(completedGame));
    }

    /** {@inheritDoc} */
    @Override
    public void leaderboard(LeaderboardSnapshotDto leaderboard) {
        dispatcher.dispatch(() -> delegate.leaderboard(leaderboard));
    }

    /** {@inheritDoc} */
    @Override
    public void serverDisconnected(String reason) {
        dispatcher.dispatch(() -> delegate.serverDisconnected(reason));
    }
}