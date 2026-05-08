package it.polimi.ingsw.client.view.tui.state;

import it.polimi.ingsw.client.view.tui.NavigationPort;
import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.client.view.tui.render.ViewTribeRenderer;

public class ViewTribeUiState implements UIState {
    private final NavigationPort nav;
    private final OutputPort out;
    private final ViewTribeRenderer renderer;
    private final String targetPlayer;

    public ViewTribeUiState(NavigationPort nav, OutputPort out, String targetPlayer) {
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
            nav.changeState(new InGameUiState(nav, out));
        } else {
            out.print("Input non valido. Premi Q per tornare alla partita.");
        }
    }

}