package it.polimi.ingsw.client.view.gui.presenter;

import it.polimi.ingsw.client.view.gui.viewstate.MatchmakingViewState;

/**
 * Screen operations required by the matchmaking presenter.
 */
public interface MatchmakingScreenPort {
    /**
     * Renders the matchmaking state.
     *
     * @param state matchmaking view state
     */
    void render(MatchmakingViewState state);
}
