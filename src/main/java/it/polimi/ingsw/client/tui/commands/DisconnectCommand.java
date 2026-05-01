package it.polimi.ingsw.client.tui.commands;

import it.polimi.ingsw.client.network.ServerController;

public class DisconnectCommand implements GameCommand {
    private final ServerController controller;

    public DisconnectCommand(ServerController controller) {
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