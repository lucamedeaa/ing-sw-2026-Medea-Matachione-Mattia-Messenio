package it.polimi.ingsw.client.tui.commands;

import it.polimi.ingsw.client.tui.NavigationPort;
import it.polimi.ingsw.client.tui.OutputPort;
import it.polimi.ingsw.client.tui.states.ViewTribeState;

public class ViewTribeCommand implements GameCommand {
    private final NavigationPort nav;
    private final OutputPort out;
    private final String targetPlayer;

    public ViewTribeCommand(NavigationPort nav, OutputPort out, String targetPlayer) {
        this.nav = nav;
        this.out = out;
        this.targetPlayer = targetPlayer;
    }

    @Override
    public void execute() {
        if (!nav.getModel().getTribes().containsKey(targetPlayer)) {
            out.print("Giocatore '" + targetPlayer + "' non trovato.");
            return;
        }
        nav.changeState(new ViewTribeState(nav, out, targetPlayer));
    }
}