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

    @Test
    @DisplayName("0 artists < upper(1) -> loses 2 PP")
    void zeroArtistsBelowThreshold() {
        Player p = new Player("Henry", TotemColor.ORANGE);
        // CavePaintings Constructor: idcard, era, upperNumArtists, decrPrestigePoints, incrPrestigePoints
        CavePaintings cp = new CavePaintings(53);

        cp.execute(List.of(p));

        assertEquals(-2, p.getPrestigePoints());
    }

    @Test
    @DisplayName("1 artist >= upper(1) -> gains 1*1=1 PP")
    void oneArtistAtThresholdGains() {
        Player p = new Player("Henry", TotemColor.ORANGE);
        // Artist Constructor: idcard, era
        p.addCard(new Artist(19));

        CavePaintings cp = new CavePaintings(53);
        cp.execute(List.of(p));

        assertEquals(1, p.getPrestigePoints());
    }

    @Test
    @DisplayName("ArtistFood adds 1 food per artist during CavePaintings")
    void artistFoodBuildingAddsFoodOnEvent() {
        Player p = new Player("Henry", TotemColor.ORANGE);
        p.addCard(new Artist(19));
        p.addCard(new Artist(20));

        // ArtistFood Constructor: idcard, foodCost, prestigePoints, era
        p.addCard(new ArtistFood(102));

        CavePaintings cp = new CavePaintings(53);
        cp.execute(List.of(p));

        assertEquals(2, p.getFood());
    }
}