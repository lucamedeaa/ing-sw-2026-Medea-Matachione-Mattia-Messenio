package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.character.Builder;
import it.polimi.ingsw.server.model.card.character.Collector;
import it.polimi.ingsw.server.model.card.character.Inventor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerEdgeCasesTest extends ModelTest {

    /**
     * SUMMARY:
     * Verifies the food-deficit-to-prestige conversion when a player with
     * some food and prestige loses more food than they have, creating a
     * partial deficit that penalises prestige.
     *
     * EXPECTATION:
     * Food resets to 0 and prestige drops from 10 to 4 (penalty of 6 for
     * the 3-unit deficit: 2 food available, 5 subtracted).
     */
    @Test
    @DisplayName("Gestione Deficit: Transizione da cibo positivo a negativo")
    void testAddFoodPartialDeficit() {
        Player p = newPlayer("Test");
        p.addPrestige(10);
        p.addFood(2);
        p.addFood(-5);
        assertEquals(0, p.getFood(), "Il cibo non deve mai scendere sotto zero");
        assertEquals(4, p.getPrestigePoints(), "Da 10 PP deve scendere a 4 PP (penalità di -6)");
    }

    /**
     * SUMMARY:
     * Verifies that calling calculateTotalScore() multiple times on the
     * same player always returns the same result, confirming the computation
     * is idempotent with no side-effects.
     *
     * EXPECTATION:
     * All three successive calls return the same value.
     */
    @Test
    @DisplayName("Idempotenza del calcolo punteggio finale")
    void testCalculateTotalScoreIdempotence() {
        Player p = newPlayer("Test");
        give(p, new Inventor(39));
        give(p, new Inventor(40));

        int firstCall = p.calculateTotalScore();
        int secondCall = p.calculateTotalScore();
        int thirdCall = p.calculateTotalScore();

        assertEquals(firstCall, secondCall);
        assertEquals(firstCall, thirdCall);
    }

    /**
     * SUMMARY:
     * Verifies the inventor scoring formula with 4 inventors each having
     * a different icon, maximising the distinct-icon multiplier.
     *
     * EXPECTATION:
     * 4 inventors × 4 distinct icons = 16 total points.
     */
    @Test
    @DisplayName("Calcolo Inventori: Interazione complessa di set e icone")
    void testComplexInventorIconMath() {
        Player p = newPlayer("Test");
        give(p, new Inventor(39)); // SPEARHEAD
        give(p, new Inventor(40)); // LEATHER
        give(p, new Inventor(41)); // BREAD
        give(p, new Inventor(42)); // CANOE

        // 4 Inventori * 4 icone diverse = 16
        assertEquals(16, p.calculateTotalScore());
    }
}