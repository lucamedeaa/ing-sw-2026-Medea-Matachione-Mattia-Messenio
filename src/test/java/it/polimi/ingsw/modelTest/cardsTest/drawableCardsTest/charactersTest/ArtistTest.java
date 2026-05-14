package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.charactersTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.character.Artist;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArtistTest extends ModelTest {

    @Test
    @DisplayName("Artist pair: 3 artists give 10 PP (integer division)")
    void artistPairScoreIsIntegerDivision() {
        Player p = newPlayer("Carol");
        // Updated Constructor: idcard, era
        p.addCard(new Artist(19));
        p.addCard(new Artist(20));
        p.addCard(new Artist(21));

        assertEquals(10, p.calculateTotalScore());
    }

    @Test
    @DisplayName("4 artists give 20 final PP")
    void fourArtistsGiveTwentyPoints() {
        Player p = newPlayer("Carol");
        p.addCard(new Artist(19));
        p.addCard(new Artist(20));
        p.addCard(new Artist(21));
        p.addCard(new Artist(22));

        assertEquals(20, p.calculateTotalScore());
    }
}