package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.charactersTest;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.characters.Hunter;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HunterTest extends ModelTest {
        /**
         * the first Hunter WITH an icon gives 1 food (only him present).
         */
        @Test
        @DisplayName("First hunter WITH icon gives exactly 1 food")
        void firstHunterWithIconGivesOneFood() {
            Player p = newPlayer("Alice");
            give(p, new Hunter(1, true));
            assertEquals(1, p.getFood(),
                    "The first hunter with an icon must give 1 food (only him in the tribe)");
        }

        /**
         * adding a second Hunter WITH an icon yields 1 food
         * for EVERY hunter present (thus 2 additional food at the time of addition).
         *
         * 1st hunter without icon (0 food) + 2nd hunter with icon → +2 food.
         */
        @Test
        @DisplayName("Second hunter WITH icon counts all present hunters")
        void secondHunterWithIconCountsAll() {
            Player p = newPlayer("Alice");
            give(p, new Hunter(1, false)); // no food
            give(p, new Hunter(1, true));  // 2 hunters in tribe → +2 food
            assertEquals(2, p.getFood());
        }

        /**
         * Adding a hunter without an icon after one with an icon MUST NOT give food.
         */
        @Test
        @DisplayName("Hunter without icon added after another gives no food")
        void addingNoIconHunterAfterIconHunterGivesNoFood() {
            Player p = newPlayer("Alice");
            give(p, new Hunter(1, true));  // +1 food
            int foodAfterFirst = p.getFood();
            give(p, new Hunter(1, false)); // should not give food
            assertEquals(foodAfterFirst, p.getFood());
        }
    }
