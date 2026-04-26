package it.polimi.ingsw.model.gameState;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.OfferTile;
import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.actions.PlaceTotemActionDTO;
import it.polimi.ingsw.network.dto.events.TotemPlacedEventDTO;

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

    /** Allows the current player to place their totem on a tile and advances the turn. @param player acting player @param tileIndex target tile index @throws IllegalStateException if it is not the player's turn */
    @Override
    public void placeTotem(Player player, int tileIndex) {
        Board board = game.getBoard();

        if (!player.equals(board.getCurrentPlayer())) {
            throw new IllegalStateException("It's not your turn to place the totem!");
        }

        board.placeTotem(tileIndex, player);

        game.notifyAll(new TotemPlacedEventDTO(player.getNickname(), tileIndex));

        board.consumeCurrentPlayer();

        if (board.allTotemsPlaced()) {
            this.transition(new ActionState(this.game));
        }
    }

    @Override
    public List<AvailableActionDTO> getAvailableActions(String playerNickname) {
        // 1. Controllo turno
        if (!playerNickname.equals(getActivePlayerNickname())) {
            return List.of();
        }

        // 2. Recupero il tracciato
        Board board = game.getBoard();
        List<OfferTile> track = board.getOfferTrack();

        // 3. Filtro gli indici delle tessere libere
        List<Integer> freeTiles = new ArrayList<>();
        for (int i = 0; i < track.size(); i++) {
            if (track.get(i).isFree()) {
                freeTiles.add(i);
            }
        }

        // 4. Inserisco la lista nel DTO
        return List.of(new PlaceTotemActionDTO(freeTiles));
    }

    @Override
    public String getActivePlayerNickname() {
        Board  board = game.getBoard();
        if (board.allTotemsPlaced()) return null;
        return board.getCurrentPlayer().getNickname();
    }
}