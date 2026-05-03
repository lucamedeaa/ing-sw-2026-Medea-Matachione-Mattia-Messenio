package it.polimi.ingsw.client.tui.commands;

import it.polimi.ingsw.client.network.ServerController;
import it.polimi.ingsw.client.tui.TUI;

public class AvailableGamesCommand implements GameCommand {
    private final ServerController controller;
    private final TUI tui;

    public AvailableGamesCommand(ServerController controller, TUI tui) {
        this.controller = controller;
        this.tui = tui;
    }

    @Override
    public void execute() {
        controller.getAvailableGames();

        tui.print("Aggiornamento lista partite in corso...");
    }
}