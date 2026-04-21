package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.charactersTest;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.characters.Inventor;
import it.polimi.ingsw.model.enums.InventorIcon;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InventorTest extends ModelTest {
        /**
         * final score = number of inventors × number of DIFFERENT icons.
         * With 3 inventors all having different icons → 3 × 3 = 9 points.
         */
        @Test
        @DisplayName("Inventor score: n_inventors × distinct_icons")
        void inventorScoreMultipliesByDistinctIcons() {
            Player p = newPlayer("Bob");
            give(p, new Inventor(1, InventorIcon.CANOE));
            give(p, new Inventor(1, InventorIcon.BREAD));
            give(p, new Inventor(1, InventorIcon.ROPE));
            // prestigePoints=0 during game, only calculateTotalScore includes inventors
            assertEquals(9, p.calculateTotalScore());
        }

        /**
         * With 2 inventors having the same icon → distinct icons = 1 → 2 × 1 = 2 points.
         */
        @Test
        @DisplayName("Inventors with the same icon count as 1 distinct icon")
        void duplicateIconCountsOnce() {
            Player p = newPlayer("Bob");
            give(p, new Inventor(1, InventorIcon.CANOE));
            give(p, new Inventor(1, InventorIcon.CANOE));
            assertEquals(2, p.calculateTotalScore()); // 2 inventors × 1 distinct icon
        }
    }
