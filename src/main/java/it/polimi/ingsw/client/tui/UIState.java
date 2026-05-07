package it.polimi.ingsw.client.tui;

public interface UIState {
    void render();
    void handleInput(String input);
}