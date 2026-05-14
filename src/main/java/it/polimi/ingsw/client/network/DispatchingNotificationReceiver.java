package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.view.ClientEventDispatcher;
import it.polimi.ingsw.common.network.dto.BoardDto;
import it.polimi.ingsw.common.network.dto.GameInfoDto;
import it.polimi.ingsw.common.network.dto.LeaderboardSnapshotDto;
import it.polimi.ingsw.common.network.dto.PlayerDto;
import it.polimi.ingsw.common.network.dto.PlayerGameCompletedDto;
import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.common.network.dto.event.GameEventDto;

import java.util.List;

public class DispatchingNotificationReceiver implements ServerNotificationReceiver {

    private final ClientEventDispatcher dispatcher;
    private final ServerNotificationReceiver delegate;

    public DispatchingNotificationReceiver(ClientEventDispatcher dispatcher, ServerNotificationReceiver delegate) {
        this.dispatcher = dispatcher;
        this.delegate = delegate;
    }

    @Override
    public void fullSync(BoardDto board, List<PlayerDto> players, String activePlayer, List<ActionDto> actions) {
        dispatcher.dispatch(() -> delegate.fullSync(board, players, activePlayer, actions));
    }

    @Override
    public void deltaEvent(List<GameEventDto> events, List<ActionDto> nextActions, String activePlayer) {
        dispatcher.dispatch(() -> delegate.deltaEvent(events, nextActions, activePlayer));
    }

    @Override
    public void error(String error) {
        dispatcher.dispatch(() -> delegate.error(error));
    }

    @Override
    public void matchmakingSuccess(String text) {
        dispatcher.dispatch(() -> delegate.matchmakingSuccess(text));
    }

    @Override
    public void availableGames(List<GameInfoDto> games) {
        dispatcher.dispatch(() -> delegate.availableGames(games));
    }

    @Override
    public void gameAborted(String reason) {
        dispatcher.dispatch(() -> delegate.gameAborted(reason));
    }

    @Override
    public void roomUpdate(String notification, List<String> currentPlayers) {
        dispatcher.dispatch(() -> delegate.roomUpdate(notification, currentPlayers));
    }

    @Override
    public void gameLeftSuccess(String text) {
        dispatcher.dispatch(() -> delegate.gameLeftSuccess(text));
    }

    @Override
    public void gameCompleted(PlayerGameCompletedDto completedGame) {
        dispatcher.dispatch(() -> delegate.gameCompleted(completedGame));
    }

    @Override
    public void leaderboard(LeaderboardSnapshotDto leaderboard) {
        dispatcher.dispatch(() -> delegate.leaderboard(leaderboard));
    }

    @Override
    public void serverDisconnected(String reason) {
        dispatcher.dispatch(() -> delegate.serverDisconnected(reason));
    }
}