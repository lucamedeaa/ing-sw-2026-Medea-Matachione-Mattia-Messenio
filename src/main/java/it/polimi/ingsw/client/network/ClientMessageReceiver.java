package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.lightGameModel.EventApplier;
import it.polimi.ingsw.client.lightGameModel.LightGameModel;
import it.polimi.ingsw.client.tui.UIState;
import it.polimi.ingsw.client.view.ClientUI;
import it.polimi.ingsw.network.client.ClientNetworkReceiver;
import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.dto.PlayerDTO;
import it.polimi.ingsw.network.messages.*;

import java.util.List;

public class ClientMessageReceiver implements ClientNetworkReceiver {
    private final LightGameModel model;
    private final EventApplier applier;
    private final ClientUI ui;

    public ClientMessageReceiver(LightGameModel model,ClientUI ui) {
        this.model = model;
        this.applier = new EventApplier(model);
        this.ui=ui;
    }

    @Override
    public void visit(FullSyncMessage msg) {
        fullSync(msg.board(), msg.players(), msg.activePlayer(), msg.actions());
    }

    @Override
    public void visit(DeltaEventMessage msg) {
        deltaEvent(msg.events(), msg.nextActions(), msg.activePlayer());
    }

    @Override
    public void visit(ErrorMessage message) {
        error(message.error());
    }

    @Override
    public void visit(ErrorMessageDTO message) {
        error(message.error());
    }

    @Override
    public void visit(MatchmakingSuccessMessage message) {
        matchmakingSuccess(message.text());
    }

    @Override
    public void visit(AvailableGamesResponseMessage message) {
        availableGames(message.games());
    }

    @Override
    public void visit(GameAbortedMessage message) {
        gameAborted(message.reason());
    }

    @Override
    public void visit(RoomUpdateMessage message) {
        roomUpdate(message.notification(), message.currentPlayers());
    }

    @Override
    public void visit(GameLeftSuccessMessage message) {
        gameLeftSuccess(message.text());
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
}
