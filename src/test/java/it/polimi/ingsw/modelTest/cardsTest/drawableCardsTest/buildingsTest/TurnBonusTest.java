package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.model.cards.drawableCards.buildings.TurnBonus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TurnBonusTest {

    private TurnBonus turnBonus;

    @BeforeEach
    void setUp() {
        turnBonus = new TurnBonus(2, 3, 1);
    }

    @Test
    void testFoodBonusIsOne() {
        assertEquals(1, turnBonus.getFoodBonus(),
                "TurnBonus deve restituire 1 come bonus di cibo");
    }

    @Test
    void testFoodBonusIsConsistent() {
        assertEquals(1, turnBonus.getFoodBonus());
        assertEquals(1, turnBonus.getFoodBonus(),
                "Chiamate multiple devono restituire sempre 1");
    }

    @Test
    void testObjectCreation() {
        assertNotNull(turnBonus,
                "L'oggetto TurnBonus deve essere creato correttamente");
    }
}