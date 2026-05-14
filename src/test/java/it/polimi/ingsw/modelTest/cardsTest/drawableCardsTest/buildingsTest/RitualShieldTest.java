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
        // Updated Constructor: idcard, foodCost, prestigePoints, era
        ritualShield = new RitualShield(96);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    @Test
    void testNegativeDecrementIsNeutralized() {
        ritualShield.onShamanicRitualEvent(player, 0, -5);

        assertEquals(5, player.getPrestigePoints(),
                "Decrement negativo deve essere annullato (-> +5)");
    }

    @Test
    void testZeroDecrementDoesNothing() {
        ritualShield.onShamanicRitualEvent(player, 0, 0);

        assertEquals(0, player.getPrestigePoints(),
                "Con decrement 0 non deve succedere nulla");
    }

    @Test
    void testIncrementIsIgnored() {
        ritualShield.onShamanicRitualEvent(player, 10, 0);

        assertEquals(0, player.getPrestigePoints(),
                "L'increment non deve essere modificato da RitualShield");
    }

    @Test
    void testBothIncrementAndDecrement() {
        ritualShield.onShamanicRitualEvent(player, 10, -5);

        assertEquals(5, player.getPrestigePoints(),
                "Deve annullare solo il decrement (-5 -> +5)");
    }

    @Test
    void testMultipleCallsAccumulate() {
        ritualShield.onShamanicRitualEvent(player, 0, -3);
        ritualShield.onShamanicRitualEvent(player, 0, -2);

        assertEquals(5, player.getPrestigePoints(),
                "Chiamate multiple devono accumularsi");
    }
}