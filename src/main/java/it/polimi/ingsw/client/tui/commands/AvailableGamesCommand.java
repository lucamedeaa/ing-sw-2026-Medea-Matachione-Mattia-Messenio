package it.polimi.ingsw.client.tui.commands;

import it.polimi.ingsw.client.network.ServerController;
import it.polimi.ingsw.client.tui.TUI;
import it.polimi.ingsw.client.tui.OutputPort;


public class AvailableGamesCommand implements GameCommand {
    private final ServerController controller;
    private final OutputPort out;

    public AvailableGamesCommand(ServerController controller, OutputPort out) {
        this.controller = controller;
        this.out = out;
    }

    @Override
    public void execute() {
        controller.getAvailableGames();

        out.print("Aggiornamento lista partite in corso...");
    }
}