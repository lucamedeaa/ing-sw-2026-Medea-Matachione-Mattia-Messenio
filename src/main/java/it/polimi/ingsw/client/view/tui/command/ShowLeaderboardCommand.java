package it.polimi.ingsw.client.view.tui.command;

public class ShowLeaderboardCommand implements GameCommand {
    private final Runnable action;

    public ShowLeaderboardCommand(Runnable action) {
        this.action = action;
    }

    @Override
    public void execute() {
        action.run();
    }
}
