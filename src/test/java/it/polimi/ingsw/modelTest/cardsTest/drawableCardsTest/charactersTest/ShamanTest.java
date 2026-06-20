package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.charactersTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.character.Shaman;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShamanTest extends ModelTest {

    /**
     * SUMMARY:
     * Verifies that adding multiple Shamans correctly sums their individual star contributions.
     *
     * EXPECTATION:
     * Two shamans with a combined total of 3 stars result in getStarsNumber returning 3.
     */
    @Test
    @DisplayName("Shaman adds correct number of stars")
    void shamanAddsStars() {
        Player p = newPlayer("Bob");
        p.addCard(new Shaman(28));
        p.addCard(new Shaman(29));

        assertEquals(3, p.getStarsNumber(), "Should sum the stars from all shamans");
    }
}