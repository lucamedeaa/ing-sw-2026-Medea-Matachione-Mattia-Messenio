package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.buildings.ArtistFood;
import it.polimi.ingsw.model.cards.drawableCards.characters.Artist;
import it.polimi.ingsw.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArtistFoodTest {

    private ArtistFood artistFood;
    private Player player;

    @BeforeEach
    void setUp() {
        artistFood = new ArtistFood(0, 0, 1);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    private void give(Player p, DrawableCard card) {
        p.addCard(card);
    }

    @Test
    void testZeroArtists() {
        artistFood.onCavePaintingsEvent(player);

        assertEquals(0, player.getFood(), "Con 0 artisti non deve essere aggiunto cibo");
    }

    @Test
    void testTwoArtistsAddTwoFood() {
        give(player, new Artist(1));
        give(player, new Artist(1));

        artistFood.onCavePaintingsEvent(player);

        assertEquals(2, player.getFood(), "Deve aggiungere 1 cibo per ogni artista");
    }

    @Test
    void testManyArtistsAddCorrectFood() {
        give(player, new Artist(1));
        give(player, new Artist(1));
        give(player, new Artist(1));
        give(player, new Artist(1));
        give(player, new Artist(1));

        artistFood.onCavePaintingsEvent(player);

        assertEquals(5, player.getFood(), "Deve funzionare anche con molti artisti");
    }

    @Test
    void testMultipleCallsAccumulateFood() {
        give(player, new Artist(1));
        give(player, new Artist(1));

        artistFood.onCavePaintingsEvent(player);
        artistFood.onCavePaintingsEvent(player);

        assertEquals(4, player.getFood(), "Il cibo deve accumularsi su chiamate multiple");
    }

    @Test
    void testNullPlayer() {
        assertThrows(NullPointerException.class, () -> artistFood.onCavePaintingsEvent(null));
    }
}