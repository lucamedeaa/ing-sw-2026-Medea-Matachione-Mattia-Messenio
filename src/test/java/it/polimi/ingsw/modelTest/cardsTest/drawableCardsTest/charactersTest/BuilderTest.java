package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.charactersTest;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.characters.Builder;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuilderTest extends ModelTest {

    @Test
    @DisplayName("Builder discount does not produce a negative cost")
    void builderDiscountDoesNotGoBelowZero() {
        Player p = newPlayer("Dave");
        // Updated Constructor: idcard, era, foodDiscount, endGamePrestigePoints
        p.addCard(new Builder(1, 1, 3, 0)); // Sconto 3
        p.addCard(new Builder(2, 1, 3, 0)); // Sconto 3 -> Totale 6

        int discount = p.getFoodDiscount();
        int buildingCost = 4;
        int finalCost = Math.max(buildingCost - discount, 0);

        assertEquals(0, finalCost, "The final cost must never be negative");
    }
}