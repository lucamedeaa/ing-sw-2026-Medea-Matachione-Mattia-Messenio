package it.polimi.ingsw.client.view.tui.command;


/**
 * Command that requests the list of games available for matchmaking.
 */
public class AvailableGamesCommand implements GameCommand {
    private final ServerCommandPort controller;

    /**
     * Creates a command that asks the server for available games.
     *
     * @param controller server command port
     */
    public AvailableGamesCommand(ServerCommandPort controller) {
        this.controller = controller;
    }

    /** {@inheritDoc} */
    @Override
    public void execute() {
        controller.getAvailableGames();
    }
}
