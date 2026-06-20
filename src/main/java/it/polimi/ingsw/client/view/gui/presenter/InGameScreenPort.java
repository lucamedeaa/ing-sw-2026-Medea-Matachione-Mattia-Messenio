package it.polimi.ingsw.client.view.gui.presenter;

import it.polimi.ingsw.client.view.gui.viewstate.GameViewState;

/**
 * Screen operations required by the in-game presenter.
 */
public interface InGameScreenPort {
    /**
     * Refreshes the screen with a complete game state.
     *
     * @param state game view state
     */
    void doRefresh(GameViewState state);

    /**
     * Shows an in-game error.
     *
     * @param error error message
     */
    void showError(String error);
}
