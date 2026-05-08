package it.polimi.ingsw.modelTest.boardTest;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.board.TileTemplate;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OfferTileTest extends ModelTest {
    /**
     * A free tile can be occupied.
     */
    @Test
    @DisplayName("Free tile: isFree() = true")
    void freeTileIsFree() {
        var tile = TileTemplate.B.createTile();
        assertTrue(tile.isFree());
    }

    /**
     * After setOccupyingPlayer, isFree() = false.
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
     * clearOccupyingPlayer restores the tile to free.
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
     * TileTemplate A: 3 bonus food, 0 upper picks, 0 lower picks.
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
     * TileTemplate G: 2 upper picks, 1 lower pick, 0 food.
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

