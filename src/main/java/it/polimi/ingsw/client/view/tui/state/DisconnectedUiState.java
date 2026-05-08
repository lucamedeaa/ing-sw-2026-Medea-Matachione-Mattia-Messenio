package it.polimi.ingsw.client.view.tui.state;

import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.client.view.tui.render.ColorAnsi;

public class DisconnectedUiState implements UIState {
    private final OutputPort out;
    private final String reason;

    public DisconnectedUiState(OutputPort out, String reason) {
        this.out = out;
        this.reason = reason;
    }

    @Override
    public void render() {
        out.clearScreen();
        out.print(ColorAnsi.BG_RED_WHITE_TEXT + " FATAL ERROR " + ColorAnsi.RESET);
        out.print(ColorAnsi.RED_BOLD + "Connessione col server interrotta." + ColorAnsi.RESET);
        out.print("Motivo: " + reason);
        out.print("\nPremi INVIO per chiudere l'applicazione.");
        out.prompt("> ");
    }

    @Override
    public void handleInput(String input) {
        System.exit(0);
    }
}