package it.polimi.ingsw.client.tui.states;

import it.polimi.ingsw.client.tui.TUI;
import it.polimi.ingsw.client.tui.UIState;

public class InfoState implements UIState {
    private final TUI tui;

    public InfoState(TUI tui) { this.tui = tui; }

    @Override
    public void render() { tui.renderCheatSheet(); }

    @Override
    public void handleInput(String input) {
        if (input.trim().equalsIgnoreCase("q")) {
            tui.changeState(new InGameState(tui));
        } else {
            tui.print("Invalid input. Press Q to return to game.");
        }
    }

    @Override
    public void onModelUpdated() { /* no-op: non interrompere la lettura */ }
}