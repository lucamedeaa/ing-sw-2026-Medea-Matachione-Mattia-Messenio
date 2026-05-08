package it.polimi.ingsw.client.view.tui.state;

public interface UIState {
    void render();
    void handleInput(String input);
}