package it.polimi.ingsw.client.view.tui.command;

import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.client.view.tui.TuiNavigator;

public class InfoCommand implements GameCommand {
    private final TuiNavigator navigator;

    public InfoCommand(TuiNavigator navigator, OutputPort out) {
        this.navigator = navigator;
    }

    @Override
    public void execute() {
        navigator.toInfo();
    }
}