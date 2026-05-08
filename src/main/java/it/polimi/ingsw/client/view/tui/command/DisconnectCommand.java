package it.polimi.ingsw.client.view.tui.command;

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