package it.polimi.ingsw.client.tui;

public class InGameState implements UIState {
    private final TUI tui;

    public InGameState(TUI tui){
        this.tui=tui;
    }

    @Override
    public void render() {
        tui.renderInGame();
    }

    @Override
    public void handleInput(String input) {

    }
}
