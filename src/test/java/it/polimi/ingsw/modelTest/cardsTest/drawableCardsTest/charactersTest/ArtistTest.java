package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.charactersTest;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.characters.Artist;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class ArtistTest extends ModelTest {
        /**
         * 10 PP for every 2 Artists (integer division).
         * 3 artists → 1 pair → 10 points. NOT 15.
         */
        @Test
        @DisplayName("Artist pair: 3 artists give 10 PP (integer division)")
        void artistPairScoreIsIntegerDivision() {
            Player p = newPlayer("Carol");
            give(p, new Artist(1));
            give(p, new Artist(1));
            give(p, new Artist(1));
            assertEquals(10, p.calculateTotalScore());
        }
        /**
         * 4 artists → 2 pairs → 20 PP.
         */
        @Test
        @DisplayName("4 artists give 20 final PP")
        void fourArtistsGiveTwentyPoints() {
            Player p = newPlayer("Carol");
            for (int i = 0; i < 4; i++) give(p, new Artist(1));
            assertEquals(20, p.calculateTotalScore());
        }

    }