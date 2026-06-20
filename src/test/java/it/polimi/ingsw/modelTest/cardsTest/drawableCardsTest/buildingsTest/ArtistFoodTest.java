package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.ArtistFood;
import it.polimi.ingsw.server.model.card.character.Artist;
import it.polimi.ingsw.server.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArtistFoodTest {

    private ArtistFood artistFood;
    private Player player;

    @BeforeEach
    void setUp() {
        artistFood = new ArtistFood(102);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    /**
     * SUMMARY:
     * Verifies that triggering the CavePaintings event with zero artists in the player's tribe adds no food.
     *
     * EXPECTATION:
     * Player's food remains 0 when no Artist cards are present.
     */
    @Test
    void testZeroArtists() {
        artistFood.onCavePaintingsEvent(player);
        assertEquals(0, player.getFood(), "Con 0 artisti non deve essere aggiunto cibo");
    }

    /**
     * SUMMARY:
     * Verifies that the ArtistFood building grants exactly 1 food per Artist when the CavePaintings event fires.
     *
     * EXPECTATION:
     * With 2 artists, the player receives 2 food.
     */
    @Test
    void testTwoArtistsAddTwoFood() {
        player.addCard(new Artist(19));
        player.addCard(new Artist(20));

        artistFood.onCavePaintingsEvent(player);
        assertEquals(2, player.getFood(), "Deve aggiungere 1 cibo per ogni artista");
    }

    /**
     * SUMMARY:
     * Verifies that ArtistFood scales correctly with a larger number of artists (5).
     *
     * EXPECTATION:
     * With 5 artists, the player receives 5 food.
     */
    @Test
    void testManyArtistsAddCorrectFood() {
        player.addCard(new Artist(19));
        player.addCard(new Artist(20));
        player.addCard(new Artist(21));
        player.addCard(new Artist(22));
        player.addCard(new Artist(23));

        artistFood.onCavePaintingsEvent(player);
        assertEquals(5, player.getFood(), "Deve funzionare anche con molti artisti");
    }

    /**
     * SUMMARY:
     * Verifies that food accumulates across multiple CavePaintings event invocations.
     *
     * EXPECTATION:
     * Two calls with 2 artists each time yield a total of 4 food.
     */
    @Test
    void testMultipleCallsAccumulateFood() {
        player.addCard(new Artist(19));
        player.addCard(new Artist(20));

        artistFood.onCavePaintingsEvent(player);
        artistFood.onCavePaintingsEvent(player);

        assertEquals(4, player.getFood(), "Il cibo deve accumularsi su chiamate multiple");
    }
}