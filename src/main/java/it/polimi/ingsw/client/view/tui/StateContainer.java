package it.polimi.ingsw.client.view.tui;

import it.polimi.ingsw.client.view.tui.state.UIState;

/**
 * Holder of the currently active TUI state.
 */
public interface StateContainer {
    /**
     * Replaces the active UI state.
     *
     * @param newState state that must become active
     */
    void updateState(UIState newState);
}
