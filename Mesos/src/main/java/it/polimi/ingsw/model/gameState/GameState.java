package it.polimi.ingsw.model.gameState;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;

public abstract class GameState {

    protected final Game game;

    public GameState(Game game) {
        this.game = game;
    }

    public abstract void start();

    protected void transition(GameState newState) {
        this.game.changeState(newState);
    }

    public void placeTotem(Player player, int tileIndex) {
        throw new IllegalStateException("Action not allowed: You cannot place a totem in this game phase.");
    }

    public void takeCard(Player player, int rowIdx, int cardIdx) {
        throw new IllegalStateException("Action not allowed: You cannot take cards in this game phase.");
    }

    public void skipBonus(Player player) {
        throw new IllegalStateException("You can't skip bonus in this phase!");
    }
}
