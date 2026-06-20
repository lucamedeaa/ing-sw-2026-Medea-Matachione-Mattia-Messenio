package it.polimi.ingsw.client.view.tui.command;

/**
 * Command that delegates leaderboard display logic to a supplied action.
 */
public class ShowLeaderboardCommand implements GameCommand {
    private final Runnable action;

    /**
     * Creates a leaderboard display command.
     *
     * @param action action to run when the command is executed
     */
    public ShowLeaderboardCommand(Runnable action) {
        this.action = action;
    }

    /** {@inheritDoc} */
    @Override
    public void execute() {
        action.run();
    }
}
