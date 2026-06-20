package it.polimi.ingsw.client.view.tui.state;

/**
 * Lifecycle contract implemented by each TUI screen state.
 */
public interface UIState {
    /**
     * Called when the state becomes active.
     */
    void onEnter();

    /**
     * Renders the state on the output port.
     */
    void render();

    /**
     * Handles one line of user input while this state is active.
     *
     * @param input raw input line
     */
    void handleInput(String input);

    /**
     * Called before the state is replaced by another one.
     */
    void onExit();
}
