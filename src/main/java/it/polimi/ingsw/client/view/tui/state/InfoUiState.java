package it.polimi.ingsw.client.view.tui.state;

import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.view.listeners.InGameView;
import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.client.view.tui.TuiNavigator;
import it.polimi.ingsw.client.view.tui.render.InfoRenderer;

/**
 * State that displays the static card and command reference while preserving in-game notifications.
 */
public class InfoUiState implements UIState, InGameView {
    private final TuiNavigator navigator;
    private final OutputPort out;
    private final InfoRenderer renderer;
    private final ClientNotificationController notificationController;

    /**
     * Creates an information state.
     *
     * @param navigator navigator used to return to the game or handle disconnections
     * @param out output port used for rendering
     * @param notificationController notification controller used to receive in-game events
     */
    public InfoUiState(TuiNavigator navigator, OutputPort out, ClientNotificationController notificationController) {
        this.navigator = navigator;
        this.out = out;
        this.renderer = new InfoRenderer(out);
        this.notificationController = notificationController;
    }

    /** {@inheritDoc} */
    @Override
    public void render() { renderer.render(); }

    /** {@inheritDoc} */
    @Override
    public void handleInput(String input) {
        if (input.trim().equalsIgnoreCase("q")) {
            navigator.toInGame();
        } else {
            out.print("Invalid input. Press Q to return to game.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onEnter() {
        if (notificationController != null) notificationController.setInGameView(this);
    }

    /** {@inheritDoc} */
    @Override
    public void onExit() {
        if (notificationController != null) notificationController.setInGameView(null);
    }

    /** {@inheritDoc} */
    @Override
    public void onReturnToMatchmaking(String reason) {
        navigator.toMatchmaking();
    }

    /** {@inheritDoc} */
    @Override
    public void onError(String error) {
        // Gli errori non bloccanti non rompono la visualizzazione delle regole
    }

    /** {@inheritDoc} */
    @Override
    public void onServerDisconnected(String reason) {
        navigator.toDisconnected(reason);
    }
}
