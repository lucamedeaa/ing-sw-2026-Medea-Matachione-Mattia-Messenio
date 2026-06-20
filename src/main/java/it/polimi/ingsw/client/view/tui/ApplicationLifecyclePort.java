package it.polimi.ingsw.client.view.tui;

/**
 * Port used by TUI components to request application-level lifecycle actions.
 */
public interface ApplicationLifecyclePort {
    /**
     * Requests a complete shutdown of the client application.
     */
    void requestShutdown();
}
