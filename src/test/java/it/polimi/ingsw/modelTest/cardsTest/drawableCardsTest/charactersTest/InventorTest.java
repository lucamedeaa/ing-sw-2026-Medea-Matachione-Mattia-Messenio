package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.charactersTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.character.Inventor;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InventorTest extends ModelTest {

    /**
     * SUMMARY:
     * Verifies that the Inventor scoring formula multiplies number of inventors by number of distinct icons.
     *
     * EXPECTATION:
     * 3 inventors with 3 distinct icons yields 9 total points.
     */
    @Test
    @DisplayName("Inventor score: n_inventors * distinct_icons")
    void inventorScoreMultipliesByDistinctIcons() {
        Player p = newPlayer("Bob");
        // Updated Constructor: idcard, era, inventorIcon
        p.addCard(new Inventor(39));
        p.addCard(new Inventor(40));
        p.addCard(new Inventor(44));

        // 3 inventors * 3 distinct icons = 9 points
        assertEquals(9, p.calculateTotalScore());
    }

    /**
     * SUMMARY:
     * Verifies that inventors sharing the same icon count as only 1 distinct icon in the scoring formula.
     *
     * EXPECTATION:
     * 2 inventors with the same icon yields 2 points (2 × 1 distinct icon).
     */
    @Test
    @DisplayName("Inventors with the same icon count as 1 distinct icon")
    void duplicateIconCountsOnce() {
        Player p = newPlayer("Bob");
        p.addCard(new Inventor(39));
        p.addCard(new Inventor(72));

        // 2 inventors * 1 distinct icon = 2 points
        assertEquals(2, p.calculateTotalScore());
    }
}