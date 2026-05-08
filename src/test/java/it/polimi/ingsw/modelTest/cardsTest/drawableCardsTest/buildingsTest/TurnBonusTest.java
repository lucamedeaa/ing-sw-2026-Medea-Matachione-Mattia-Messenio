package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.server.model.card.building.TurnBonus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TurnBonusTest {

    private TurnBonus turnBonus;

    @BeforeEach
    void setUp() {
        // Updated Constructor: idcard, foodCost, prestigePoints, era
        turnBonus = new TurnBonus(99, 3, 3, 1);
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