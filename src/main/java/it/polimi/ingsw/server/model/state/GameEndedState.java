package it.polimi.ingsw.server.model.state;

import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.exception.InvalidGameActionException;
import it.polimi.ingsw.server.model.update.AvailableAction;

import java.util.List;

/** Final state reached after a game has ended. No gameplay operation is allowed. */
public class GameEndedState extends GameState {

    public GameEndedState(Game game) {
        super(game);
    }

    @Override
    public void start() {
    }

    @Override
    public boolean isEnded() {
        return true;
    }

    @Override
    public void placeTotem(Player player, int tileIndex) {
        throw new InvalidGameActionException("Game has already ended.");
    }

    @Override
    public void takeCard(Player player, int rowIdx, int cardIdx) {
        throw new InvalidGameActionException("Game has already ended.");
    }

    @Override
    public void skipBonus(Player player) {
        throw new InvalidGameActionException("Game has already ended.");
    }

    @Override
    public List<AvailableAction> getAvailableActions(String nickName) {
        return List.of();
    }

    @Override
    public String getActivePlayerNickname() {
        return null;
    }
}
