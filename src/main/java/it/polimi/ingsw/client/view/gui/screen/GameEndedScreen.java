package it.polimi.ingsw.client.view.gui.screen;

import it.polimi.ingsw.client.view.gui.RefreshableScreen;
import it.polimi.ingsw.client.view.listeners.GameEndedView;

public class GameEndedScreen implements GameEndedView, RefreshableScreen {
    @Override
    public void onReturnToMatchmaking(String reason) {

    }

    @Override
    public void onServerDisconnected(String reason) {

    }

    @Override
    public void refresh() {

    }
    //TODO: Controller FXML della schermata fine partita. Implementa GameEndedView e UIObserver.
    // Non chiama mai addObserver. In initialize() chiama controller.getLeaderboard();
    // quando il server risponde, ClientNotificationController aggiorna GameModel → notifyUI() → router → onStateChanged()
    // già sul thread JavaFX → legge il leaderboard e popola la tabella. Mostra spinner finché il dato non arriva.
    //GameEndedScreen deve renderizzare subito il risultato locale anche se la leaderboard globale non è ancora arrivata.
}
