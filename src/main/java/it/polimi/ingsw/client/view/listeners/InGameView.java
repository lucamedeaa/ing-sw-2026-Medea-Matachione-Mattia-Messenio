package it.polimi.ingsw.client.view.listeners;

/** Defines the contract for in game view. */
public interface InGameView {
    void onReturnToMatchmaking(String reason);
    void onError(String error);
    void onServerDisconnected(String reason);
}