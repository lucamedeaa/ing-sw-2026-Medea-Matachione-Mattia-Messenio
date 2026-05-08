package it.polimi.ingsw.client.view.tui.command;

import it.polimi.ingsw.client.view.tui.NavigationPort;
import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.client.view.tui.state.InfoUiState;

public class InfoCommand implements GameCommand {
    private final NavigationPort nav;
    private final OutputPort out;

    public InfoCommand(NavigationPort nav, OutputPort out) {
        this.nav = nav;
        this.out = out;
    }

    @Override
    public void execute() {
        nav.changeState(new InfoUiState(nav, out));
    }
}