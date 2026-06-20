package it.polimi.ingsw.client.view.gui.screen;

import javafx.stage.WindowEvent;

/**
 * Lifecycle contract implemented by GUI screens loaded by the router.
 */
public interface RefreshableScreen {
    /**
     * Refreshes the screen from its presenter.
     */
    void refresh();

    /**
     * Called when the screen becomes active.
     */
    default void onEnter() {}

    /**
     * Called before the screen is replaced.
     */
    default void onExit() {}

    /**
     * Handles a JavaFX window close event.
     *
     * @param event close event
     */
    default void handleWindowClose(WindowEvent event) {}
}
