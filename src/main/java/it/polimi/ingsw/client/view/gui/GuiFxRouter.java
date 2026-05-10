package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.model.UIObserver;

public class GuiFxRouter implements UIObserver,GuiNavigator {
    private RefreshableScreen currentScreen;
   //TODO:  Implementa GuiNavigator e UIObserver. Unica classe che conosce lo Stage, cambia scena e osserva gameModel.
   // Si registra su gameModel una volta nel costruttore — mai rimosso.
   // Tiene currentScreenObserver: solo toInGame() e toGameEnded() lo impostano con la screen corrente, tutti gli altri lo impostano a null.
   // Quando onStateChanged() arriva dal thread di rete, fa Platform.runLater(() -> currentScreenObserver.onStateChanged()) —
   // parallelo esatto di TextUserInterface.onStateChanged() nella TUI.
   //Per ogni metodo toXxx(): 1. Carica FXML 2. Ottiene il controller 3. Se la screen deve ricevere refresh dal GameModel, assegna currentScreenObserver = controller 4. Altrimenti currentScreenObserver = null 5. stage.setScene(...)

    @Override
    public void onStateChanged() {

    }
}
