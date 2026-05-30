package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.charactersTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.character.Artist;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArtistTest extends ModelTest {

    /**
     * SUMMARY:
     * Verifies that 3 artists yield 10 prestige points, confirming integer division in pair scoring.
     *
     * EXPECTATION:
     * calculateTotalScore returns 10 for 3 artists (1 complete pair + 1 remainder).
     */
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

    /**
     * SUMMARY:
     * Verifies that 4 artists yield 20 prestige points, confirming the pair-based scoring formula.
     *
     * EXPECTATION:
     * calculateTotalScore returns 20 for 4 artists (2 complete pairs).
     */
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