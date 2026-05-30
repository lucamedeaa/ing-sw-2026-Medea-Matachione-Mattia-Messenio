package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.DoublePrestigeShaman;
import it.polimi.ingsw.server.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DoublePrestigeShamanTest {

    private DoublePrestigeShaman doublePrestigeShaman;
    private Player player;

    @BeforeEach
    void setUp() {
        // Updated Constructor: idcard, foodCost, prestigePoints, era
        doublePrestigeShaman = new DoublePrestigeShaman(105);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    /**
     * SUMMARY:
     * Verifies that DoublePrestigeShaman doubles the prestige gained by adding the positive increment again.
     *
     * EXPECTATION:
     * Player gains the increment value as extra prestige, and the method returns 0.
     */
    @Test
    void testPositiveIncrementAddsPrestigeAgain() {
        int result = doublePrestigeShaman.onShamanicRitualEvent(player, 10, -5);

        assertEquals(10, player.getPrestigePoints(),
                "Con increment positivo deve aggiungere nuovamente quei punti prestigio");
        assertEquals(0, result, "Il metodo deve restituire sempre 0");
    }

    /**
     * SUMMARY:
     * Verifies that DoublePrestigeShaman does not add any prestige when the increment is zero.
     *
     * EXPECTATION:
     * Player's prestige stays at 0, and the method returns 0.
     */
    @Test
    void testZeroIncrementAddsNothing() {
        int result = doublePrestigeShaman.onShamanicRitualEvent(player, 0, -5);

        assertEquals(0, player.getPrestigePoints(),
                "Con increment uguale a 0 non deve aggiungere prestigio");
        assertEquals(0, result, "Il metodo deve restituire sempre 0");
    }

    /**
     * SUMMARY:
     * Verifies that DoublePrestigeShaman ignores the decrement parameter entirely.
     *
     * EXPECTATION:
     * Prestige remains unaffected by the decrement value; method returns 0.
     */
    @Test
    void testDecrementIsIgnored() {
        int result = doublePrestigeShaman.onShamanicRitualEvent(player, 0, -10);

        assertEquals(0, player.getPrestigePoints(),
                "Il decrement non deve avere effetto in questa carta");
        assertEquals(0, result, "Il metodo deve restituire sempre 0");
    }

    /**
     * SUMMARY:
     * Verifies that prestige bonuses from DoublePrestigeShaman accumulate over multiple event calls.
     *
     * EXPECTATION:
     * Two calls with increment 5 each result in 10 total prestige points.
     */
    @Test
    void testMultipleCallsAccumulatePrestige() {
        doublePrestigeShaman.onShamanicRitualEvent(player, 5, -2);
        doublePrestigeShaman.onShamanicRitualEvent(player, 5, -2);

        assertEquals(10, player.getPrestigePoints(),
                "Chiamate multiple con increment positivo devono accumularsi");
    }
}