package it.polimi.ingsw.client.tui.states;

import it.polimi.ingsw.client.tui.NavigationPort;
import it.polimi.ingsw.client.tui.OutputPort;
import it.polimi.ingsw.client.tui.UIState;
import it.polimi.ingsw.client.tui.render.ViewTribeRenderer;

import java.util.List;

public class ViewTribeState implements UIState {
    private final NavigationPort nav;
    private final OutputPort out;
    private final ViewTribeRenderer renderer;
    private final String targetPlayer;

    public ViewTribeState(NavigationPort nav, OutputPort out, String targetPlayer) {
        this.nav = nav;
        this.out = out;
        this.targetPlayer = targetPlayer;
        this.renderer = new ViewTribeRenderer(out);
        //render();
    }

    public void render() {
        nav.getMatchModel().getReadLock().lock();
        try {
            var tribe = nav.getMatchModel().getTribes().get(targetPlayer);
            renderer.render(targetPlayer, tribe);
        } finally {
            nav.getMatchModel().getReadLock().unlock();
        }
    }

    @Override
    public void handleInput(String input) {
        if (input.trim().equalsIgnoreCase("q")) {
            nav.changeState(new InGameState(nav, out));
        } else {
            out.print("Input non valido. Premi Q per tornare alla partita.");
        }
    }

}