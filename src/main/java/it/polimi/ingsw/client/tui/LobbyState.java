package it.polimi.ingsw.client.tui;

public class LobbyState implements UIState {
    private final TUI tui;

    public LobbyState(TUI tui){
        this.tui=tui;
    }
    @Override
    public void render() {
        tui.renderLobby(,);
    }

    @Override
    public void handleInput(String input) {

    }
}
