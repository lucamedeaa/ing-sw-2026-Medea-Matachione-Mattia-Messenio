package it.polimi.ingsw.model.gameState;

import it.polimi.ingsw.model.Factory.TileFactory;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.board.OfferTile;

import java.util.List;

public class InitState extends GameState {

    public InitState(Game game) {
        super(game);
    }

    @Override
    public void start() {
        this.transition(new PlacementState(this.game));
    }
}