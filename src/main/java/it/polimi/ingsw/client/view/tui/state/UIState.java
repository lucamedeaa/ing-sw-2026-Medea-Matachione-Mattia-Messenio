package it.polimi.ingsw.client.view.tui.state;

public interface UIState {
    void onEnter();
    void render();
    void handleInput(String input);
    void onExit();
}