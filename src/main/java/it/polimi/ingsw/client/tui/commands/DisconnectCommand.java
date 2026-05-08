package it.polimi.ingsw.client.tui.commands;

import it.polimi.ingsw.client.network.ServerController;
import it.polimi.ingsw.client.tui.ServerCommandPort;

public class DisconnectCommand implements GameCommand {
    private final ServerCommandPort controller;

    public DisconnectCommand(ServerCommandPort controller) {
        this.controller = controller;
    }

    @Override
    public void execute() {
        controller.disconnect(() -> {
            System.out.println("Disconnessione completata.");
            System.exit(0);
        });
    }
}