package it.polimi.ingsw.server.model.state;

import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.exception.InvalidGameActionException;
import it.polimi.ingsw.server.model.update.AvailableAction;

import java.util.List;

/** Final state reached after a game has ended. No gameplay operation is allowed. */
public class GameEndedState extends GameState {

    /**
     * Creates a new {@code GameEndedState} instance.
     *
     * @param game game
     */
    public GameEndedState(Game game) {
        super(game);
    }

    /** {@inheritDoc} */
    @Override
    public void start() {
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEnded() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void placeTotem(Player player, int tileIndex) {
        throw new InvalidGameActionException("Game has already ended.");
    }

    /** {@inheritDoc} */
    @Override
    public void takeCard(Player player, int rowIdx, int cardIdx) {
        throw new InvalidGameActionException("Game has already ended.");
    }

    /** {@inheritDoc} */
    @Override
    public void skipBonus(Player player) {
        throw new InvalidGameActionException("Game has already ended.");
    }

    /** {@inheritDoc} */
    @Override
    public List<AvailableAction> getAvailableActions(String nickName) {
        return List.of();
    }

    /** {@inheritDoc} */
    @Override
    public String getActivePlayerNickname() {
        return null;
    }
}
