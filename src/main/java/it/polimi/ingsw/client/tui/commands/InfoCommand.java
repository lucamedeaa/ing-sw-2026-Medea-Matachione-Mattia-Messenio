package it.polimi.ingsw.client.tui.commands;

import it.polimi.ingsw.client.tui.TUI;
import it.polimi.ingsw.client.tui.states.InfoState;

public class InfoCommand implements GameCommand {
    private final TUI tui;

    public InfoCommand(TUI tui) {
        this.tui = tui;
    }

    @Override
    public void execute() {
        tui.changeState(new InfoState(tui));
    }
}