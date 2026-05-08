package it.polimi.ingsw.client.view.listeners;

public interface InGameView {
    void onDeltaEvent();
    void onReturnToMatchmaking(String reason);
    void onError(String error);
    void onServerDisconnected(String reason);
}