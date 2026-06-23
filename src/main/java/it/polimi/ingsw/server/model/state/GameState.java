package it.polimi.ingsw.server.model.state;

import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.exception.InvalidGameActionException;
import it.polimi.ingsw.server.model.update.AvailableAction;

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

    /**
     * Returns whether ended.
     *
     * @return true if ended; false otherwise
     */
    public boolean isEnded() {
        return false;
    }

    /** Attempts to place a totem; by default not allowed. @param player acting player @param tileIndex target tile index */
    public void placeTotem(Player player, int tileIndex) {
        throw new InvalidGameActionException("Action not allowed: You cannot place a totem in this game phase.");
    }

    /** Attempts to take a card; by default not allowed. @param player acting player @param rowIdx row index @param cardIdx column index */
    public void takeCard(Player player, int rowIdx, int cardIdx) {
        throw new InvalidGameActionException("Action not allowed: You cannot take cards in this game phase.");
    }

    /** Attempts to skip bonus; by default not allowed. @param player acting player */
    public void skipBonus(Player player) {
        throw new InvalidGameActionException("You can't skip bonus in this phase!");
    }

    /**
     * Returns the available actions.
     *
     * @param nickName player nickname
     * @return the available actions
     */
    public abstract List<AvailableAction> getAvailableActions(String nickName);

    /**
     * Returns the active player nickname.
     *
     * @return the active player nickname
     */
    public abstract String getActivePlayerNickname();
}
