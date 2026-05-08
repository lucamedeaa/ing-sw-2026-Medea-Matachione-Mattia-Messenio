package it.polimi.ingsw.client.tui.states;

import it.polimi.ingsw.client.tui.OutputPort;
import it.polimi.ingsw.client.tui.UIState;
import it.polimi.ingsw.client.tui.render.AnsiColors;

public class DisconnectedState implements UIState {
    private final OutputPort out;
    private final String reason;

    public DisconnectedState(OutputPort out, String reason) {
        this.out = out;
        this.reason = reason;
    }

    @Override
    public void render() {
        out.clearScreen();
        out.print(AnsiColors.BG_RED_WHITE_TEXT + " FATAL ERROR " + AnsiColors.RESET);
        out.print(AnsiColors.RED_BOLD + "Connessione col server interrotta." + AnsiColors.RESET);
        out.print("Motivo: " + reason);
        out.print("\nPremi INVIO per chiudere l'applicazione.");
        out.prompt("> ");
    }

    @Override
    public void handleInput(String input) {
        System.exit(0);
    }
}