package it.polimi.ingsw.model.gameState;

import it.polimi.ingsw.model.Game;

public class InitState extends GameState {

    public InitState(Game game) {
        super(game);
    }

    @Override
    public void start() {
        game.getBoard().setupBoard(game.getPlayers());
        this.transition(new PlacementState(this.game));
    }
}