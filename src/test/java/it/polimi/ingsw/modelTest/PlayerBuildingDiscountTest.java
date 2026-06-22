package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.character.Builder;
import it.polimi.ingsw.server.model.card.character.Collector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerBuildingDiscountTest extends ModelTest {

    /**
     * SUMMARY:
     * Verifies that the food discount (getFoodDiscount) only counts Builder
     * cards and the sustenance discount (getSustenanceDiscount) only counts
     * Collector cards, ensuring the two discount types are properly isolated.
     *
     * EXPECTATION:
     * getFoodDiscount() returns 2 (from Builder) and getSustenanceDiscount()
     * returns 3 (from Collector), with no cross-contamination.
     */
    @Test
    @DisplayName("Discount Isolation: Collectors must not discount Building purchases")
    void testBuilderAndCollectorDiscountSeparation() {
        Player p = newPlayer("TestPlayer");

        // 2 units of discount for purchasing Buildings
        give(p, new Builder(2));

        // 3 units of discount for Sustenance events
        give(p, new Collector(3));

        assertEquals(2, p.getFoodDiscount(),
                "The building purchase discount (getFoodDiscount) must only count Builders. Collectors were erroneously included.");

        // Sustenance must receive the correct discount, ignoring the Builder
        assertEquals(3, p.getSustenanceDiscount(),
                "The sustenance discount (getSustenanceDiscount) must only count Collectors. Builders were erroneously included or double-counted.");
    }
}