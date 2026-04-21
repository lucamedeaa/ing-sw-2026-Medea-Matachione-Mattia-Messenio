package it.polimi.ingsw.model.gameState;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Board;

public class PlacementState extends GameState {

    public PlacementState(Game game) {
        super(game);
    }

    @Override
    public void start() {
    }

    @Override
    public void placeTotem(Player player, int tileIndex) {
        Board board = game.getBoard();

        if (!player.equals(board.getCurrentPlayer())) {
            throw new IllegalStateException("It's not your turn to place the totem!");
        }

        board.placeTotem(tileIndex, player);

        board.consumeCurrentPlayer();

        if (board.allTotemsPlaced()) {
            this.transition(new ActionState(this.game));
        }
    }
}
