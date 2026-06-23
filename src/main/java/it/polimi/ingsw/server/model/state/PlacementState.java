package it.polimi.ingsw.server.model.state;

import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.board.OfferTile;
import it.polimi.ingsw.server.model.exception.InvalidGameActionException;
import it.polimi.ingsw.server.model.update.AvailableAction;
import it.polimi.ingsw.server.model.update.AvailableAction.*;
import it.polimi.ingsw.server.model.update.GameEvent.*;

import java.util.ArrayList;
import java.util.List;

/** Game state handling the totem placement phase, where players place their totems on the offer track in turn order. */
public class PlacementState extends GameState {

    /** Constructs the placement state. @param game the game instance */
    public PlacementState(Game game) {
        super(game);
    }

    /** Initializes the placement phase. */
    @Override
    public void start() {
    }

    /** Allows the current player to place their totem on a tile and advances the turn. @param player acting player @param tileIndex target tile index */
    @Override
    public void placeTotem(Player player, int tileIndex) {
        Board board = game.getBoard();

        if (!player.equals(board.getCurrentPlayer())) {
            throw new InvalidGameActionException("It's not your turn to place the totem!");
        }

        board.placeTotem(tileIndex, player);



        board.consumeCurrentPlayer();
        game.pushEvent(new TotemPlacedEvent(player.getNickname(), tileIndex));

        if (board.allTotemsPlaced()) {
            this.transition(new ActionState(this.game));
        }

    }

    /** {@inheritDoc} */
    @Override
    public List<AvailableAction> getAvailableActions(String playerNickname) {

        if (!playerNickname.equals(getActivePlayerNickname())) {
            return List.of();
        }


        Board board = game.getBoard();
        List<OfferTile> track = board.getOfferTrack();


        List<Integer> freeTiles = new ArrayList<>();
        for (int i = 0; i < track.size(); i++) {
            if (track.get(i).isFree()) {
                freeTiles.add(i);
            }
        }

        return List.of(new PlaceTotemAction(freeTiles));
    }

    /** {@inheritDoc} */
    @Override
    public String getActivePlayerNickname() {
        Board  board = game.getBoard();
        if (board.allTotemsPlaced()) return null;
        return board.getCurrentPlayer().getNickname();
    }
}
