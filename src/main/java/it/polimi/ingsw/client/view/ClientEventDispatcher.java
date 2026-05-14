package it.polimi.ingsw.client.view;

public interface ClientEventDispatcher {
    void dispatch(Runnable task);
}