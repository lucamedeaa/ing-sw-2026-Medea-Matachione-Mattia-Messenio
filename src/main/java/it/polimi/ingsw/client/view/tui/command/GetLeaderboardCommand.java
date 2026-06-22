package it.polimi.ingsw.client.view.tui.command;

/**
 * Command that requests the leaderboard from the server.
 */
public class GetLeaderboardCommand implements GameCommand {
    private final ServerCommandPort server;

    /**
     * Creates a leaderboard request command.
     *
     * @param server server command port
     */
    public GetLeaderboardCommand(ServerCommandPort server) {
        this.server = server;
    }

    /** {@inheritDoc} */
    @Override
    public void execute() {
        server.getLeaderboard();
    }
}
