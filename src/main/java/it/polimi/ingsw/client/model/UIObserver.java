package it.polimi.ingsw.client.model;

/**
 * Observer notified when a client model changes and the UI should refresh.
 */
public interface UIObserver {
    /**
     * Called after the observed model changes.
     */
    void onStateChanged();
}
