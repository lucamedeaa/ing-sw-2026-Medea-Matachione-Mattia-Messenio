package it.polimi.ingsw.client.view.tui.command;

import it.polimi.ingsw.client.view.tui.OutputPort;


public class AvailableGamesCommand implements GameCommand {
    private final ServerCommandPort controller;

    public AvailableGamesCommand(ServerCommandPort controller, OutputPort out) {
        this.controller = controller;
    }

    @Override
    public void execute() {
        controller.getAvailableGames();
    }
}