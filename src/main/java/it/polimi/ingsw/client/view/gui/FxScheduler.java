package it.polimi.ingsw.client.view.gui;

@FunctionalInterface
public interface FxScheduler {
    void runLater(Runnable task);
}