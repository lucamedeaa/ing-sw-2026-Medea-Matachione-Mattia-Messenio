package it.polimi.ingsw.client.view.tui.state;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.client.view.tui.TuiNavigator;
import it.polimi.ingsw.client.view.tui.render.ViewTribeRenderer;

public class ViewTribeUiState implements UIState {
    private final TuiNavigator navigator;
    private final GameModel gameModel;
    private final OutputPort out;
    private final ViewTribeRenderer renderer;
    private final String targetPlayer;

    public ViewTribeUiState(TuiNavigator navigator, GameModel gameModel, OutputPort out, String targetPlayer) {
        this.navigator = navigator;
        this.gameModel = gameModel;
        this.out = out;
        this.targetPlayer = targetPlayer;
        this.renderer = new ViewTribeRenderer(out);
    }

    @Override
    public void render() {
        gameModel.getReadLock().lock();
        try {
            var tribe = gameModel.getTribes().get(targetPlayer);
            renderer.render(targetPlayer, tribe);
        } finally { gameModel.getReadLock().unlock(); }
    }

    @Override
    public void handleInput(String input) {
        if (input.trim().equalsIgnoreCase("q")) {
            navigator.toInGame();
        } else {
            out.print("Invalid input. Press Q to return to the game.");
        }
    }

}