package it.polimi.ingsw.modelTest.cardsTest.eventsTest;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.buildings.ArtistFood;
import it.polimi.ingsw.model.cards.drawableCards.characters.Artist;
import it.polimi.ingsw.model.cards.events.CavePaintings;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CavePaintingsTest extends ModelTest {
    @Test
    @DisplayName("0 artists < upper(1) → loses 2 PP")
    void zeroArtistsBelowThreshold() {
        Player p = newPlayer("Henry");
        CavePaintings cp = new CavePaintings(1, 1, -2, 1);
        cp.execute(List.of(p));
        assertEquals(-2, p.getPrestigePoints());
    }

    @Test
    @DisplayName("1 artist >= upper(1) → gains 1×1=1 PP")
    void oneArtistAtThresholdGains() {
        Player p = newPlayer("Henry");
        give(p, new Artist(1));
        CavePaintings cp = new CavePaintings(1, 1, -2, 1);
        cp.execute(List.of(p));
        assertEquals(1, p.getPrestigePoints());
    }

    @Test
    @DisplayName("ArtistFood adds 1 food per artist during CavePaintings")
    void artistFoodBuildingAddsFoodOnEvent() {
        Player p = newPlayer("Henry");
        give(p, new Artist(1));
        give(p, new Artist(1));
        give(p, new ArtistFood(0, 0, 1));
        CavePaintings cp = new CavePaintings(1, 1, -2, 1);
        cp.execute(List.of(p));
        assertEquals(2, p.getFood());
    }
}
