package it.polimi.ingsw.client.view.gui.presenter;

import it.polimi.ingsw.client.view.gui.viewstate.GameEndedViewState;

/**
 * Screen operations required by the game-ended presenter.
 */
public interface GameEndedScreenPort {
    /**
     * Renders the game-ended state.
     *
     * @param state state to render
     */
    void render(GameEndedViewState state);

    /**
     * Shows the global leaderboard loading spinner.
     */
    void showSpinner();
}
