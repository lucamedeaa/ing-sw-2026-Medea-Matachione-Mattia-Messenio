package it.polimi.ingsw.client.view.tui.command;

public class DisconnectCommand implements GameCommand {
    private final ServerCommandPort controller;

    public DisconnectCommand(ServerCommandPort controller) {
        this.controller = controller;
    }

    @Override
    public void execute() {
        controller.disconnect(() -> {
            System.out.println("Logout complete.");
            System.exit(0);
        });
    }
}