package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.model.cards.drawableCards.buildings.LatePurchase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LatePurchaseTest {

    @Test
    void testTopRowBonusIsOne() {
        LatePurchase latePurchase = new LatePurchase(2, 3, 1);
        assertEquals(1, latePurchase.getTopRowBonus());
    }
}