package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.RitualStars;
import it.polimi.ingsw.server.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RitualStarsTest {

    private RitualStars ritualStars;
    private Player player;

    @BeforeEach
    void setUp() {
        ritualStars = new RitualStars(104);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    /**
     * SUMMARY:
     * Verifies that RitualStars returns 3 additional stars when called with zero increment and decrement.
     *
     * EXPECTATION:
     * The method returns exactly 3.
     */
    @Test
    void testReturnsThree() {
        int result = ritualStars.onShamanicRitualEvent(player, 0, 0);

        assertEquals(3, result,
                "RitualStars deve sempre restituire 3");
    }

    /**
     * SUMMARY:
     * Verifies that the increment parameter does not affect RitualStars' return value.
     *
     * EXPECTATION:
     * The method returns 3 regardless of a non-zero increment.
     */
    @Test
    void testReturnsThreeRegardlessOfIncrement() {
        int result = ritualStars.onShamanicRitualEvent(player, 10, 0);

        assertEquals(3, result,
                "Il valore restituito non dipende da increment");
    }

    /**
     * SUMMARY:
     * Verifies that the decrement parameter does not affect RitualStars' return value.
     *
     * EXPECTATION:
     * The method returns 3 regardless of a non-zero decrement.
     */
    @Test
    void testReturnsThreeRegardlessOfDecrement() {
        int result = ritualStars.onShamanicRitualEvent(player, 0, -5);

        assertEquals(3, result,
                "Il valore restituito non dipende da decrement");
    }

    /**
     * SUMMARY:
     * Verifies that RitualStars does not modify any player state (prestige or food) as a side effect.
     *
     * EXPECTATION:
     * Player's prestige and food remain 0 after calling onShamanicRitualEvent.
     */
    @Test
    void testDoesNotModifyPlayer() {
        ritualStars.onShamanicRitualEvent(player, 10, -5);

        assertEquals(0, player.getPrestigePoints(),
                "RitualStars non deve modificare il prestigio del player");
        assertEquals(0, player.getFood(),
                "RitualStars non deve modificare il cibo del player");
    }
}