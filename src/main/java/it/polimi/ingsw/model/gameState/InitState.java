package it.polimi.ingsw.model.gameState;

import it.polimi.ingsw.model.Game;

/** Initial game state that immediately transitions to the placement phase. */
public class InitState extends GameState {

    /** Constructs the initial state. @param game the game instance */
    public InitState(Game game) {
        super(game);
    }

    /** Starts the game by transitioning to the placement state. */
    @Override
    public void start() {
        this.transition(new PlacementState(this.game));
    }
}