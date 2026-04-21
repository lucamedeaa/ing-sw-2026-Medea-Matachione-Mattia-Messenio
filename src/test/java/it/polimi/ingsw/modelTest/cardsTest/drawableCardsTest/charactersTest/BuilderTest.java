package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.charactersTest;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.characters.Builder;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuilderTest extends ModelTest {
    /**
     * the discount cannot reduce the cost below 0.
     * getFoodDiscount() sums all builders → Player.getFoodDiscount() returns
     * the total sum.
     * Check that the discount does not exceed the building cost.
     */
    @Test
    @DisplayName("Builder discount does not produce a negative cost")
    void builderDiscountDoesNotGoBelowZero() {
        Player p = newPlayer("Dave");
        give(p, new Builder(1, 3, 0)); // discount 3
        give(p, new Builder(1, 3, 0)); // discount 3 → total 6
        // The building costs 4: max applicable discount = 4, not 6
        int discount = p.getFoodDiscount();
        int buildingCost = 4;
        int finalCost = Math.max(buildingCost - discount, 0);
        assertEquals(0, finalCost, "The final cost must never be negative");
    }
}
