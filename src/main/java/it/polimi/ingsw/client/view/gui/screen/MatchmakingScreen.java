package it.polimi.ingsw.client.view.gui.screen;

import it.polimi.ingsw.client.view.gui.RefreshableScreen;
import it.polimi.ingsw.client.view.listeners.MatchmakingView;
import it.polimi.ingsw.common.network.dto.GameInfoDto;

import java.util.List;

public class MatchmakingScreen implements MatchmakingView, RefreshableScreen {
    @Override
    public void onAvailableGames(List<GameInfoDto> games) {

    }

    @Override
    public void onMatchmakingSuccess(String text) {

    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onServerDisconnected(String reason) {

    }

    @Override
    public void refresh() {

    }
    //TODO: Controller FXML della schermata iniziale. Implementa MatchmakingView.
    // Si auto-registra in initialize() e si de-registra prima di navigare.
    // I bottoni Crea/Unisciti/Aggiorna chiamano ServerController.
    // Tutti i callback arrivano dal thread di rete — Platform.runLater obbligatorio.
}
