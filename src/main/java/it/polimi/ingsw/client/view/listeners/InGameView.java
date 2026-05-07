package it.polimi.ingsw.client.view.listeners;

public interface InGameView {
    void onDeltaEvent();
    void onGameAborted(String reason);
    void onError(String error);
}