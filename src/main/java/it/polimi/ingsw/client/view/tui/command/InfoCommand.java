package it.polimi.ingsw.client.view.tui.command;

import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.client.view.tui.TuiNavigator;

/**
 * Command that opens the in-game card reference screen.
 */
public class InfoCommand implements GameCommand {
    private final TuiNavigator navigator;

    /**
     * Creates a command that navigates to the information screen.
     *
     * @param navigator TUI navigator
     */
    public InfoCommand(TuiNavigator navigator) {
        this.navigator = navigator;
    }

    /** {@inheritDoc} */
    @Override
    public void execute() {
        navigator.toInfo();
    }
}
