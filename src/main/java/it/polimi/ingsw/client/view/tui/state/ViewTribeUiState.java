package it.polimi.ingsw.client.view.tui.state;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.view.listeners.InGameView;
import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.client.view.tui.TuiNavigator;
import it.polimi.ingsw.client.view.tui.render.ViewTribeRenderer;

/**
 * State that displays a selected player's tribe during a match.
 */
public class ViewTribeUiState implements UIState, InGameView {
    private final TuiNavigator navigator;
    private final GameModel gameModel;
    private final OutputPort out;
    private final ViewTribeRenderer renderer;
    private final String targetPlayer;
    private final ClientNotificationController notificationController;

    /**
     * Creates a tribe inspection state.
     *
     * @param navigator navigator used to return to the game or handle disconnections
     * @param gameModel model containing tribe data
     * @param out output port used for rendering
     * @param targetPlayer nickname of the inspected player
     * @param notificationController notification controller used to receive in-game events
     */
    public ViewTribeUiState(TuiNavigator navigator, GameModel gameModel, OutputPort out, String targetPlayer, ClientNotificationController notificationController) {
        this.navigator = navigator;
        this.gameModel = gameModel;
        this.out = out;
        this.targetPlayer = targetPlayer;
        this.renderer = new ViewTribeRenderer(out);
        this.notificationController = notificationController;

    }

    /** {@inheritDoc} */
    @Override
    public void render() {

            var tribe = gameModel.getTribes().get(targetPlayer);
            renderer.render(targetPlayer, tribe);
    }

    /** {@inheritDoc} */
    @Override
    public void handleInput(String input) {
        if (input.trim().equalsIgnoreCase("q")) {
            navigator.toInGame();
        } else {
            out.print("Invalid input. Press Q to return to the game.");
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
    public void onError(String error) {}

    /** {@inheritDoc} */
    @Override
    public void onServerDisconnected(String reason) {
        navigator.toDisconnected(reason);
    }
}
