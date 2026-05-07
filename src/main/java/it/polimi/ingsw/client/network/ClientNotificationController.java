package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.lightGameModel.EventApplier;
import it.polimi.ingsw.client.lightGameModel.LightGameModel;
import it.polimi.ingsw.client.tui.UIState;
import it.polimi.ingsw.client.view.ClientUI;
import it.polimi.ingsw.network.client.ServerNotificationReceiver;
import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.LeaderboardSnapshot;
import it.polimi.ingsw.network.dto.PlayerGameCompletedDTO;
import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.dto.PlayerDTO;
import it.polimi.ingsw.network.messages.GameInfoDTO;

import java.util.List;

public class ClientNotificationController implements ServerNotificationReceiver {
    private final LightGameModel model;
    private final EventApplier applier;
    private final ClientUI ui;

    public ClientNotificationController(LightGameModel model, EventApplier applier, ClientUI ui) {
        this.model = model;
        this.applier = applier;
        this.ui = ui;
    }

    @Override
    public void fullSync(BoardDTO board, List<PlayerDTO> players, String activePlayer, List<AvailableActionDTO> actions) {
        model.setFullState(board, players, activePlayer);
        model.setAvailableActions(actions);
        ui.dispatch(UIState::onGameStarted);
    }

    @Override
    public void deltaEvent(List<GameEventDTO> events, List<AvailableActionDTO> nextActions, String activePlayer) {
        model.startBatch();
        for (GameEventDTO event : events) {
            event.accept(applier);
        }
        model.setAvailableActions(nextActions);
        model.setActivePlayer(activePlayer);
        model.endBatch();
    }

    @Override
    public void error(String error) {
        ui.dispatch(state -> state.onError(error));
    }

    @Override
    public void matchmakingSuccess(String text) {
        ui.dispatch(state -> state.onMatchmakingSuccess(text));
    }

    @Override
    public void availableGames(List<GameInfoDTO> games) {
        ui.dispatch(state -> state.onAvailableGames(games));
    }

    @Override
    public void gameAborted(String reason) {
        ui.dispatch(state -> state.onGameAborted(reason));
    }

    @Override
    public void roomUpdate(String notification, List<String> currentPlayers) {
        ui.dispatch(state -> state.onRoomUpdate(currentPlayers, notification));
    }

    @Override
    public void gameLeftSuccess(String text) {
        ui.dispatch(UIState::onGameLeft);
    }

    @Override
    public void gameCompleted(PlayerGameCompletedDTO completedGame) {
        // TODO implement client-side completed-game handling.
    }

    @Override
    public void leaderboard(LeaderboardSnapshot leaderboard) {
        // TODO implement client-side leaderboard handling.
    }
}
