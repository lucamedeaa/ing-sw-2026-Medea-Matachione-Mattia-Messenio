package it.polimi.ingsw.server.model.board;
import it.polimi.ingsw.server.model.Player;
import java.util.Optional;

/**
 * A slot on the offer track where a player can place their totem.
 * Defines how many cards the occupying player may pick from each row
 * and what food bonus they receive.
 */
public class OfferTile {

    private final char letterId;
    private Optional<Player> occupyingPlayer;
    private final int upperRowPicks;
    private final int lowerRowPicks;
    private final int foodBonus;

    /**
     * @param letterId      identifies the tile variant (e.g. 'A', 'B', ...)
     * @param upperRowPicks number of cards the player may take from the upper row
     * @param lowerRowPicks number of cards the player may take from the lower row
     * @param foodBonus     food awarded when the player occupies this tile
     */
    public OfferTile(char letterId, int upperRowPicks, int lowerRowPicks, int foodBonus) {
        this.letterId = letterId;
        this.upperRowPicks = upperRowPicks;
        this.lowerRowPicks = lowerRowPicks;
        this.foodBonus = foodBonus;
        this.occupyingPlayer = Optional.empty();
    }

    /** @return true if no player is currently occupying this tile */
    public Boolean isFree() {
        return occupyingPlayer.isEmpty();
    }

    /** Places {@code player} on this tile. Passing null clears the occupant. */
    public void setOccupyingPlayer(Player player) {
        this.occupyingPlayer = Optional.ofNullable(player);
    }

    /** Frees the tile, removing any occupying player. */
    public void clearOccupyingPlayer() {
        this.occupyingPlayer = Optional.empty();
    }

    public Optional<Player> getOccupyingPlayer() { return occupyingPlayer; }
    public int getUpperRowPicks() { return upperRowPicks; }
    public int getLowerRowPicks() { return lowerRowPicks; }
    public char getLetterId() { return letterId; }
    public int getFoodBonus() { return foodBonus; }
}