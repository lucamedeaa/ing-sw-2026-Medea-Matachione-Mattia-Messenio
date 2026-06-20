package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.RitualShield;
import it.polimi.ingsw.server.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RitualShieldTest {

    private RitualShield ritualShield;
    private Player player;

    @BeforeEach
    void setUp() {
        ritualShield = new RitualShield(96);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    /**
     * SUMMARY:
     * Verifies that RitualShield neutralizes a negative decrement, converting the lost prestige into gained prestige.
     *
     * EXPECTATION:
     * Player gains 5 prestige points (the absolute value of the -5 decrement).
     */
    @Test
    void testNegativeDecrementIsNeutralized() {
        ritualShield.onShamanicRitualEvent(player, 0, -5);

        assertEquals(5, player.getPrestigePoints(),
                "Decrement negativo deve essere annullato (-> +5)");
    }

    /**
     * SUMMARY:
     * Verifies that RitualShield does not modify prestige when the decrement is zero.
     *
     * EXPECTATION:
     * Player's prestige remains 0.
     */
    @Test
    void testZeroDecrementDoesNothing() {
        ritualShield.onShamanicRitualEvent(player, 0, 0);

        assertEquals(0, player.getPrestigePoints(),
                "Con decrement 0 non deve succedere nulla");
    }

    /**
     * SUMMARY:
     * Verifies that RitualShield does not process or react to the increment parameter.
     *
     * EXPECTATION:
     * Player's prestige remains 0 despite a non-zero increment value.
     */
    @Test
    void testIncrementIsIgnored() {
        ritualShield.onShamanicRitualEvent(player, 10, 0);

        assertEquals(0, player.getPrestigePoints(),
                "L'increment non deve essere modificato da RitualShield");
    }

    /**
     * SUMMARY:
     * Verifies that RitualShield neutralizes only the decrement while leaving the increment unprocessed.
     *
     * EXPECTATION:
     * Player gains 5 prestige (from neutralized -5 decrement); increment is not handled by this card.
     */
    @Test
    void testBothIncrementAndDecrement() {
        ritualShield.onShamanicRitualEvent(player, 10, -5);

        assertEquals(5, player.getPrestigePoints(),
                "Deve annullare solo il decrement (-5 -> +5)");
    }

    /**
     * SUMMARY:
     * Verifies that prestige gained from neutralized decrements accumulates across multiple calls.
     *
     * EXPECTATION:
     * Two calls neutralizing -3 and -2 yield a total of 5 prestige points.
     */
    @Test
    void testMultipleCallsAccumulate() {
        ritualShield.onShamanicRitualEvent(player, 0, -3);
        ritualShield.onShamanicRitualEvent(player, 0, -2);

        assertEquals(5, player.getPrestigePoints(),
                "Chiamate multiple devono accumularsi");
    }
}