package it.polimi.ingsw.client.view;

/** Defines the contract for client event dispatcher. */
public interface ClientEventDispatcher {
    void dispatch(Runnable task);
}