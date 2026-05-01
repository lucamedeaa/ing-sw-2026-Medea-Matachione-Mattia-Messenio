package it.polimi.ingsw.client.tui.commands;

import it.polimi.ingsw.client.tui.TUI;
import it.polimi.ingsw.client.tui.states.ViewTribeState;

public class ViewTribeCommand implements GameCommand {
    private final TUI tui;
    private final String targetPlayer;

    public ViewTribeCommand(TUI tui, String targetPlayer) {
        this.tui = tui;
        this.targetPlayer = targetPlayer;
    }

    @Override
    public void execute() {
        if (!tui.getModel().getTribes().containsKey(targetPlayer)) {
            tui.print("Giocatore '" + targetPlayer + "' non trovato.");
            return;
        }
        tui.changeState(new ViewTribeState(tui, targetPlayer));
    }
}