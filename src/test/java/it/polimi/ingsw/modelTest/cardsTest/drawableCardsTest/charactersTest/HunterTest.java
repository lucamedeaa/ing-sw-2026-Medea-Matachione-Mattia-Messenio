package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.charactersTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.character.Hunter;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HunterTest extends ModelTest {

    /**
     * SUMMARY:
     * Verifies that the first Hunter with an icon grants exactly 1 food (counts itself in the tribe).
     *
     * EXPECTATION:
     * Player receives 1 food after adding the first hunter with an icon.
     */
    @Test
    @DisplayName("First hunter WITH icon gives exactly 1 food")
    void firstHunterWithIconGivesOneFood() {
        Player p = newPlayer("Alice");
        p.addCard(new Hunter(10));

        assertEquals(1, p.getFood(),
                "The first hunter with an icon must give 1 food (only him in the tribe)");
    }

    /**
     * SUMMARY:
     * Verifies that a second Hunter with an icon counts all present hunters in the tribe for food.
     *
     * EXPECTATION:
     * Player receives 2 food total (the second icon-hunter counts both hunters).
     */
    @Test
    @DisplayName("Second hunter WITH icon counts all present hunters")
    void secondHunterWithIconCountsAll() {
        Player p = newPlayer("Alice");
        p.addCard(new Hunter(12)); // no food
        p.addCard(new Hunter(10));  // 2 hunters in tribe -> +2 food

        assertEquals(2, p.getFood());
    }

    /**
     * SUMMARY:
     * Verifies that adding a Hunter without an icon does not grant any additional food.
     *
     * EXPECTATION:
     * Player's food remains unchanged after adding a no-icon hunter.
     */
    @Test
    @DisplayName("Hunter without icon added after another gives no food")
    void addingNoIconHunterAfterIconHunterGivesNoFood() {
        Player p = newPlayer("Alice");
        p.addCard(new Hunter(10));  // +1 food
        int foodAfterFirst = p.getFood();

        p.addCard(new Hunter(12)); // should not give food

        assertEquals(foodAfterFirst, p.getFood());
    }
}