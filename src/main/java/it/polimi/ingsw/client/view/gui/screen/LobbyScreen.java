package it.polimi.ingsw.client.view.gui.screen;

import it.polimi.ingsw.client.view.gui.RefreshableScreen;
import it.polimi.ingsw.client.view.listeners.LobbyView;

import java.util.List;

public class LobbyScreen implements LobbyView, RefreshableScreen {
    @Override
    public void onRoomUpdate(String notification, List<String> currentPlayers) {

    }

    @Override
    public void onGameStarted() {

    }

    @Override
    public void onReturnToMatchmaking(String reason) {

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
    //TODO:  Controller FXML della sala d'attesa. Implementa LobbyView.
    // Mostra la lista giocatori e la notifica di sala ricevuta via onRoomUpdate.
    // Su onGameStarted naviga a toInGame(), su onReturnToMatchmaking torna al matchmaking.
    // De-registra sempre prima di navigare. Tutti i callback arrivano dal thread di rete — Platform.runLater obbligatorio.
}
