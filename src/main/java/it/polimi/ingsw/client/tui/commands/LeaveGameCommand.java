package it.polimi.ingsw.client.tui.commands;

import it.polimi.ingsw.client.network.ServerController;
import it.polimi.ingsw.client.tui.TUI;

public class LeaveGameCommand implements GameCommand {
    private final ServerController controller;
    private final TUI tui;

    public LeaveGameCommand(ServerController controller, TUI tui) {
        this.controller = controller;
        this.tui = tui;
    }

    @Override
    public void execute() {
        controller.leaveGame();

        tui.print("Richiesta di uscita dalla lobby inviata...");
    }
}