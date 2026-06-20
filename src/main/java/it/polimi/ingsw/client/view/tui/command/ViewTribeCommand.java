package it.polimi.ingsw.client.view.tui.command;

import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.client.view.tui.TuiNavigator;

/**
 * Command that opens the tribe inspection screen for a player.
 */
public class ViewTribeCommand implements GameCommand {
    private final TuiNavigator navigator;
    private final String targetPlayer;

    /**
     * Creates a command that navigates to a player's tribe.
     *
     * @param navigator TUI navigator
     * @param out output port reserved for command feedback
     * @param targetPlayer nickname of the player to inspect
     */
    public ViewTribeCommand(TuiNavigator navigator, OutputPort out, String targetPlayer) {
        this.navigator = navigator;
        this.targetPlayer = targetPlayer;
    }

    /** {@inheritDoc} */
    @Override
    public void execute() {
        navigator.toViewTribe(targetPlayer);
    }
}
