package it.polimi.ingsw.client.view.gui;

/**
 * Abstraction for scheduling work on the JavaFX application thread.
 */
@FunctionalInterface
public interface FxScheduler {
    /**
     * Schedules a task to run later on the JavaFX application thread.
     *
     * @param task task to execute
     */
    void runLater(Runnable task);
}
