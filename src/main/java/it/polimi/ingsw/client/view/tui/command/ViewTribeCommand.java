package it.polimi.ingsw.client.view.tui.command;

import it.polimi.ingsw.client.view.tui.NavigationPort;
import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.client.view.tui.state.ViewTribeUiState;

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
        if (!nav.getMatchModel().getTribes().containsKey(targetPlayer)) {
            out.print("Giocatore '" + targetPlayer + "' non trovato.");
            return;
        }
        nav.changeState(new ViewTribeUiState(nav, out, targetPlayer));
    }
}