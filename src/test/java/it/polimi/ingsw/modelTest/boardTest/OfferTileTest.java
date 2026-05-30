package it.polimi.ingsw.modelTest.boardTest;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.board.TileTemplate;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OfferTileTest extends ModelTest {
    /**
     * SUMMARY:
     * Verifies that a newly created offer tile is initially free (not occupied by any player).
     *
     * EXPECTATION:
     * isFree() returns true on a freshly created tile.
     */
    @Test
    @DisplayName("Free tile: isFree() = true")
    void freeTileIsFree() {
        var tile = TileTemplate.B.createTile();
        assertTrue(tile.isFree());
    }

    /**
     * SUMMARY:
     * Verifies that after setting an occupying player on a tile, it is no longer free.
     *
     * EXPECTATION:
     * isFree() returns false after setOccupyingPlayer is called.
     */
    @Test
    @DisplayName("Occupied tile: isFree() = false")
    void occupiedTileIsNotFree() {
        var tile = TileTemplate.B.createTile();
        Player p = newPlayer("Test");
        tile.setOccupyingPlayer(p);
        assertFalse(tile.isFree());
    }

    /**
     * SUMMARY:
     * Verifies that clearOccupyingPlayer restores a previously occupied tile back to free.
     *
     * EXPECTATION:
     * isFree() returns true after calling clearOccupyingPlayer on an occupied tile.
     */
    @Test
    @DisplayName("clearOccupyingPlayer frees the tile")
    void clearOccupyingPlayerMakesTileFree() {
        var tile = TileTemplate.C.createTile();
        Player p = newPlayer("Test");
        tile.setOccupyingPlayer(p);
        tile.clearOccupyingPlayer();
        assertTrue(tile.isFree());
    }

    /**
     * SUMMARY:
     * Verifies that TileTemplate A creates a tile with the correct food bonus and zero card picks.
     *
     * EXPECTATION:
     * The tile has foodBonus=3, upperRowPicks=0, and lowerRowPicks=0.
     */
    @Test
    @DisplayName("TileTemplate A has foodBonus=3 and no picks")
    void tileTemplateAHasCorrectValues() {
        var tile = TileTemplate.A.createTile();
        assertEquals(3, tile.getFoodBonus());
        assertEquals(0, tile.getUpperRowPicks());
        assertEquals(0, tile.getLowerRowPicks());
    }

    /**
     * SUMMARY:
     * Verifies that TileTemplate G creates a tile with the correct card pick counts and zero food bonus.
     *
     * EXPECTATION:
     * The tile has upperRowPicks=2, lowerRowPicks=1, and foodBonus=0.
     */
    @Test
    @DisplayName("TileTemplate G has 2 upper + 1 lower picks")
    void tileTemplateGHasCorrectPicks() {
        var tile = TileTemplate.G.createTile();
        assertEquals(2, tile.getUpperRowPicks());
        assertEquals(1, tile.getLowerRowPicks());
        assertEquals(0, tile.getFoodBonus());
    }
}

