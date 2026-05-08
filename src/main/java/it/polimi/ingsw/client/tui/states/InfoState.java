package it.polimi.ingsw.client.tui.states;

import it.polimi.ingsw.client.tui.NavigationPort;
import it.polimi.ingsw.client.tui.OutputPort;
import it.polimi.ingsw.client.tui.TUI;
import it.polimi.ingsw.client.tui.UIState;
import it.polimi.ingsw.client.tui.render.InfoRenderer;

public class InfoState implements UIState {
    private final NavigationPort nav;
    private final OutputPort out;
    private final InfoRenderer renderer;

    public InfoState(NavigationPort nav, OutputPort out) {
        this.nav = nav;
        this.out = out;
        this.renderer = new InfoRenderer(out);
        //render();
    }

    public void render() {
        renderer.render(); // Mostra il cheat sheet
    }

    @Override
    public void handleInput(String input) {
        if (input.trim().equalsIgnoreCase("q")) {
            nav.changeState(new InGameState(nav, out));
        } else {
            out.print("Invalid input. Press Q to return to game.");
        }
    }

}