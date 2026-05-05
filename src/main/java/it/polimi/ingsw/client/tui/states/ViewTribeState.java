package it.polimi.ingsw.client.tui.states;

import it.polimi.ingsw.client.tui.TUI;
import it.polimi.ingsw.client.tui.UIState;

public class ViewTribeState implements UIState {
    private final TUI tui;
    private final String targetPlayer;

    public ViewTribeState(TUI tui, String targetPlayer) {
        this.tui = tui;
        this.targetPlayer = targetPlayer;
    }

    @Override
    public void render() {
        tui.renderViewTribe(targetPlayer);
    }

    @Override
    public void handleInput(String input) {
        if (input.trim().equalsIgnoreCase("q")) {
            tui.changeState(new InGameState(tui));
        } else {
            tui.print("Input non valido. Premi Q per tornare alla partita.");
        }
    }

    @Override
    public void onModelUpdated() {
        // no-op: ignora gli aggiornamenti in background per non interrompere la visualizzazione
    }
}