package it.polimi.ingsw.model.gameState;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.updates.AvailableAction;

import java.util.List;

/** Final state reached after a game has ended. No gameplay operation is allowed. */
public class GameEndedState extends GameState {

    public GameEndedState(Game game) {
        super(game);
    }

    @Override
    public void start() {
        // No initialization is needed for a terminal state.
    }

    @Override
    public boolean isEnded() {
        return true;
    }

    @Override
    public void placeTotem(Player player, int tileIndex) {
        throw new IllegalStateException("Game has already ended.");
    }

    @Override
    public void takeCard(Player player, int rowIdx, int cardIdx) {
        throw new IllegalStateException("Game has already ended.");
    }

    @Override
    public void skipBonus(Player player) {
        throw new IllegalStateException("Game has already ended.");
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
