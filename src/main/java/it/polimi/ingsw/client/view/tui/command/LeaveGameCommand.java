package it.polimi.ingsw.client.view.tui.command;

import it.polimi.ingsw.client.view.tui.OutputPort;

/**
 * Command that leaves the current game or lobby and returns to matchmaking.
 */
public class LeaveGameCommand implements GameCommand {
    private final ServerCommandPort controller;
    private final OutputPort out;

    /**
     * Creates a leave command.
     *
     * @param controller server command port
     * @param out output port used for user feedback
     */
    public LeaveGameCommand(ServerCommandPort controller, OutputPort out) {
        this.controller = controller;
        this.out = out;
    }

    /** {@inheritDoc} */
    @Override
    public void execute() {
        controller.leaveGame(); // o leaveMatch() se hai implementato la separazione
        out.print("Withdrawal request sent...");
    }
}
