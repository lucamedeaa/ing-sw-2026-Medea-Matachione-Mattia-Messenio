package it.polimi.ingsw.client.view.gui;

public interface RefreshableScreen {
    void refresh();
    default void handleWindowClose(javafx.stage.WindowEvent event, GuiContext ctx, GuiNavigator navigator) {
        javafx.application.Platform.exit();
        System.exit(0);
    }
}
// Contratto di refresh interno usato esclusivamente da GuiFxRouter.
// Le screen che implementano questa interfaccia (MatchmakingScreen, LobbyScreen,
// InGameScreen, GameEndedScreen) non sono observer del model: non chiamano mai
// addObserver() su nessun modello. Implementano RefreshableScreen solo per
// ricevere la chiamata refresh() dal router quando gameModel o lobbyModel
// notificano un cambiamento via UIObserver.onStateChanged().
// Separare questo contratto da UIObserver evita l'ambiguità: vedere una screen
// che "implementa UIObserver" farebbe pensare che si registri sul model,
// il che non è mai vero nella GUI.