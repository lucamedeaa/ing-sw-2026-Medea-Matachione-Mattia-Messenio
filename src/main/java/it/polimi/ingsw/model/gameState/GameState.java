package it.polimi.ingsw.model.gameState;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.updates.AvailableAction;
import it.polimi.ingsw.network.dto.AvailableActionDTO;

import java.util.List;

/** Abstract base class for all game states. Defines lifecycle, transitions, and default invalid actions. */
public abstract class GameState {

    protected final Game game;

    /** Constructs a game state. @param game the game instance */
    public GameState(Game game) {
        this.game = game;
    }

    /** Initializes the state when it becomes active. */
    public abstract void start();

    /** Transitions the game to a new state. @param newState next game state */
    protected void transition(GameState newState) {
        this.game.changeState(newState);
    }

    public boolean isEnded() {
        return false;
    }

    /** Attempts to place a totem; by default not allowed. @param player acting player @param tileIndex target tile index @throws IllegalStateException always */
    public void placeTotem(Player player, int tileIndex) {
        throw new IllegalStateException("Action not allowed: You cannot place a totem in this game phase.");
    }

    /** Attempts to take a card; by default not allowed. @param player acting player @param rowIdx row index @param cardIdx column index @throws IllegalStateException always */
    public void takeCard(Player player, int rowIdx, int cardIdx) {
        throw new IllegalStateException("Action not allowed: You cannot take cards in this game phase.");
    }

    /** Attempts to skip bonus; by default not allowed. @param player acting player @throws IllegalStateException always */
    public void skipBonus(Player player) {
        throw new IllegalStateException("You can't skip bonus in this phase!");
    }

    public abstract List<AvailableAction> getAvailableActions(String nickName);

    public abstract String getActivePlayerNickname();
}