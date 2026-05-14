package it.polimi.ingsw.client.view.tui.state;

import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.view.listeners.InGameView;
import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.client.view.tui.TuiNavigator;
import it.polimi.ingsw.client.view.tui.render.InfoRenderer;

public class InfoUiState implements UIState, InGameView {
    private final TuiNavigator navigator;
    private final OutputPort out;
    private final InfoRenderer renderer;
    private final ClientNotificationController notificationController;

    public InfoUiState(TuiNavigator navigator, OutputPort out, ClientNotificationController notificationController) {
        this.navigator = navigator;
        this.out = out;
        this.renderer = new InfoRenderer(out);
        this.notificationController = notificationController;
    }

    @Override
    public void render() { renderer.render(); }

    @Override
    public void handleInput(String input) {
        if (input.trim().equalsIgnoreCase("q")) {
            navigator.toInGame();
        } else {
            out.print("Invalid input. Press Q to return to game.");
        }
    }

    @Override
    public void onEnter() {
        if (notificationController != null) notificationController.setInGameView(this);
    }

    @Override
    public void onExit() {
        if (notificationController != null) notificationController.setInGameView(null);
    }

    @Override
    public void onReturnToMatchmaking(String reason) {
        navigator.toMatchmaking();
    }

    @Override
    public void onError(String error) {
        // Gli errori non bloccanti non rompono la visualizzazione delle regole
    }

    @Override
    public void onServerDisconnected(String reason) {
        navigator.toDisconnected(reason);
    }
}