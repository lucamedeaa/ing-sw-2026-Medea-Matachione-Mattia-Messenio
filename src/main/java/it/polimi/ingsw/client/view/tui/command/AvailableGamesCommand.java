package it.polimi.ingsw.client.view.tui.command;

import it.polimi.ingsw.client.view.tui.OutputPort;


/**
 * Command that requests the list of games available for matchmaking.
 */
public class AvailableGamesCommand implements GameCommand {
    private final ServerCommandPort controller;

    /**
     * Creates a command that asks the server for available games.
     *
     * @param controller server command port
     * @param out output port reserved for command feedback
     */
    public AvailableGamesCommand(ServerCommandPort controller, OutputPort out) {
        this.controller = controller;
    }

    /** {@inheritDoc} */
    @Override
    public void execute() {
        controller.getAvailableGames();
    }
}
