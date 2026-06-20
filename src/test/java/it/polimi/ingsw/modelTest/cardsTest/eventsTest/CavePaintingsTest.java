package it.polimi.ingsw.modelTest.cardsTest.eventsTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.ArtistFood;
import it.polimi.ingsw.server.model.card.character.Artist;
import it.polimi.ingsw.server.model.card.event.CavePaintings;
import it.polimi.ingsw.server.model.enums.TotemColor;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CavePaintingsTest extends ModelTest {

    /**
     * SUMMARY:
     * Verifies that a player with 0 artists (below threshold) loses 2 prestige points during CavePaintings.
     *
     * EXPECTATION:
     * Player's prestige drops to -2.
     */
    @Test
    @DisplayName("0 artists < upper(1) -> loses 2 PP")
    void zeroArtistsBelowThreshold() {
        Player p = new Player("Henry", TotemColor.ORANGE);
        CavePaintings cp = new CavePaintings(53);

        cp.execute(List.of(p));

        assertEquals(-2, p.getPrestigePoints());
    }

    /**
     * SUMMARY:
     * Verifies that a player with artists at or above the threshold gains prestige during CavePaintings.
     *
     * EXPECTATION:
     * Player gains 1 prestige point with 1 artist meeting the upper threshold.
     */
    @Test
    @DisplayName("1 artist >= upper(1) -> gains 1*1=1 PP")
    void oneArtistAtThresholdGains() {
        Player p = new Player("Henry", TotemColor.ORANGE);
        p.addCard(new Artist(19));

        CavePaintings cp = new CavePaintings(53);
        cp.execute(List.of(p));

        assertEquals(1, p.getPrestigePoints());
    }

    /**
     * SUMMARY:
     * Verifies that the ArtistFood building triggers during CavePaintings, adding 1 food per artist.
     *
     * EXPECTATION:
     * Player receives 2 food (1 per each of the 2 artists) from the ArtistFood building effect.
     */
    @Test
    @DisplayName("ArtistFood adds 1 food per artist during CavePaintings")
    void artistFoodBuildingAddsFoodOnEvent() {
        Player p = new Player("Henry", TotemColor.ORANGE);
        p.addCard(new Artist(19));
        p.addCard(new Artist(20));
        p.addCard(new ArtistFood(102));

        CavePaintings cp = new CavePaintings(53);
        cp.execute(List.of(p));

        assertEquals(2, p.getFood());
    }
}