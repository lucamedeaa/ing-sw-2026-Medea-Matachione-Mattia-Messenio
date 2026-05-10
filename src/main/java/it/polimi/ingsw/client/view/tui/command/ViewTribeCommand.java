package it.polimi.ingsw.client.view.tui.command;

import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.client.view.tui.TuiNavigator;

public class ViewTribeCommand implements GameCommand {
    private final TuiNavigator navigator;
    private final String targetPlayer;

    public ViewTribeCommand(TuiNavigator navigator, OutputPort out, String targetPlayer) {
        this.navigator = navigator;
        this.targetPlayer = targetPlayer;
    }

    @Override
    public void execute() {
        navigator.toViewTribe(targetPlayer);
    }
}