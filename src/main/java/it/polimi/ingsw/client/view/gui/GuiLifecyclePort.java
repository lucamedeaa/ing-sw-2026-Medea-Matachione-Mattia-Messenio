package it.polimi.ingsw.client.view.gui;

/**
 * Port used by GUI components to request application shutdown.
 */
public interface GuiLifecyclePort {
    /**
     * Requests shutdown of the GUI application.
     */
    void requestShutdown();
}
