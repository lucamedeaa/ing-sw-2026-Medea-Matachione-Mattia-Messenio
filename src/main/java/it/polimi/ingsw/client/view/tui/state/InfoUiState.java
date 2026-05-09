package it.polimi.ingsw.client.view.tui.state;

import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.client.view.tui.TuiNavigator;
import it.polimi.ingsw.client.view.tui.render.InfoRenderer;

public class InfoUiState implements UIState {
    private final TuiNavigator navigator;
    private final OutputPort out;
    private final InfoRenderer renderer;

    public InfoUiState(TuiNavigator navigator, OutputPort out) {
        this.navigator = navigator;
        this.out = out;
        this.renderer = new InfoRenderer(out);
    }

    @Override
    public void render() { renderer.render(); }

    @Override
    public void handleInput(String input) {
        if (input.trim().equalsIgnoreCase("q")) {
            navigator.toInGame();
        } else {
            out.print("Invalid input. Press Q to return to game.");
        }
    }
}