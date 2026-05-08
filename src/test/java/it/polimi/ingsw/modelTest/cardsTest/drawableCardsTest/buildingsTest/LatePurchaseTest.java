package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.server.model.card.building.LatePurchase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LatePurchaseTest {

    @Test
    void testTopRowBonusIsOne() {
        // Updated Constructor: idcard, foodCost, prestigePoints, era
        LatePurchase latePurchase = new LatePurchase(110, 9, 3, 3);
        assertEquals(1, latePurchase.getTopRowBonus());
    }
}