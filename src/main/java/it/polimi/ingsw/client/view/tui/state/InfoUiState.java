package it.polimi.ingsw.client.view.tui.state;

import it.polimi.ingsw.client.view.tui.NavigationPort;
import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.client.view.tui.render.InfoRenderer;

public class InfoUiState implements UIState {
    private final NavigationPort nav;
    private final OutputPort out;
    private final InfoRenderer renderer;

    public InfoUiState(NavigationPort nav, OutputPort out) {
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
            nav.changeState(new InGameUiState(nav, out));
        } else {
            out.print("Invalid input. Press Q to return to game.");
        }
    }

}