package it.polimi.ingsw.client.view.gui.presenter;

import it.polimi.ingsw.client.view.gui.viewstate.LobbyViewState;

/**
 * Screen operations required by the lobby presenter.
 */
public interface LobbyScreenPort {
    /**
     * Renders the lobby state.
     *
     * @param state lobby view state
     */
    void render(LobbyViewState state);

    /**
     * Shows a lobby error.
     *
     * @param error error message
     */
    void showError(String error);
}
