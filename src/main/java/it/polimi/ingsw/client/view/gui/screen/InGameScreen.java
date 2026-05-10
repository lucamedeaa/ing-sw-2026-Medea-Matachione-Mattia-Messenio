package it.polimi.ingsw.client.view.gui.screen;


import it.polimi.ingsw.client.view.gui.RefreshableScreen;
import it.polimi.ingsw.client.view.listeners.InGameView;

public class InGameScreen implements InGameView, RefreshableScreen {

    @Override
    public void onDeltaEvent() {

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
    //TODO: Controller FXML della schermata di gioco. Implementa InGameView e UIObserver. Non chiama mai addObserver — lo fa il router.
    // onStateChanged() è già sul thread JavaFX (il router lo wrappa in Platform.runLater) quindi aggiorna direttamente i sotto-pannelli inclusi via <fx:include>.
    // Se isGameOver() naviga a toGameEnded(). Apre ViewTribeScreen e InfoScreen come Stage modali.
    // I callback InGameView arrivano dal thread di rete — Platform.runLater obbligatorio. onDeltaEvent() è no-op
    //in InGameScreen.onStateChanged()/render devi mantenere una guardia come nella TUI:
    // if (gameModel.getPlayers().isEmpty()) {
    // return;}
}
