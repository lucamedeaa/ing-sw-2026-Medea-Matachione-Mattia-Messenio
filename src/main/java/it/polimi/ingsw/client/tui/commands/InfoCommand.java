package it.polimi.ingsw.client.tui.commands;

import it.polimi.ingsw.client.tui.NavigationPort;
import it.polimi.ingsw.client.tui.OutputPort;
import it.polimi.ingsw.client.tui.states.InfoState;

public class InfoCommand implements GameCommand {
    private final NavigationPort nav;
    private final OutputPort out;

    public InfoCommand(NavigationPort nav, OutputPort out) {
        this.nav = nav;
        this.out = out;
    }

    @Override
    public void execute() {
        nav.changeState(new InfoState(nav, out));
    }
}