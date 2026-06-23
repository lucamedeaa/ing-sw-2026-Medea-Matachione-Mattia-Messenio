package it.polimi.ingsw.client.view.listeners;

/** Defines the contract for game ended view. */
public interface GameEndedView {
    void onReturnToMatchmaking(String reason);
    void onServerDisconnected(String reason);
}