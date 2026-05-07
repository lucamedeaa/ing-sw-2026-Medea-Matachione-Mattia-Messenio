package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.lightGameModel.EventApplier;
import it.polimi.ingsw.client.lightGameModel.LightGameModel;
import it.polimi.ingsw.client.tui.UIState;
import it.polimi.ingsw.client.view.ClientUI;
import it.polimi.ingsw.network.client.ServerNotificationReceiver;
import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.dto.PlayerDTO;
import it.polimi.ingsw.network.messages.GameInfoDTO;

import java.util.List;

public class ClientNotificationController implements ServerNotificationReceiver {
    private final LightGameModel model;
    private final EventApplier applier;

    // Nota: ho rimosso ClientUI dal costruttore, non ci serve più!
    public ClientNotificationController(LightGameModel model, EventApplier applier) {
        this.model = model;
        this.applier = applier;
    }

    @Override
    public void fullSync(BoardDTO board, List<PlayerDTO> players, String activePlayer, List<AvailableActionDTO> actions) {
        model.setFullState(board, players, activePlayer);
        model.setAvailableActions(actions);
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
        model.setGlobalError(error);
    }

    @Override
    public void matchmakingSuccess(String text) {
        model.setLobbyData(model.getLobbyPlayers(), text);
    }

    @Override
    public void availableGames(List<GameInfoDTO> games) {
        model.setAvailableGames(games);
    }

    @Override
    public void gameAborted(String reason) {
        model.setGameAborted(reason);
    }

    @Override
    public void roomUpdate(String notification, List<String> currentPlayers) {
        model.setLobbyData(currentPlayers, notification);
    }

    @Override
    public void gameLeftSuccess(String text) {
        model.setGameAborted(text); // Ricicliamo la logica di abort per far disconnettere agilmente la TUI e tornare al menu
    }
}
