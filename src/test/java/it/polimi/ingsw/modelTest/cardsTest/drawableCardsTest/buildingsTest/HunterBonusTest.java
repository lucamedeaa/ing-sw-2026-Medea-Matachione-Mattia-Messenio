package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.HunterBonus;
import it.polimi.ingsw.server.model.card.character.Hunter;
import it.polimi.ingsw.server.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HunterBonusTest {

    private HunterBonus hunterBonus;
    private Player player;

    @BeforeEach
    void setUp() {
        hunterBonus = new HunterBonus(108);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    /**
     * SUMMARY:
     * Verifies that the HunterBonus building grants no food or prestige when the player has no Hunter cards.
     *
     * EXPECTATION:
     * Both food and prestige points remain 0.
     */
    @Test
    void testNoHuntersGivesNothing() {
        hunterBonus.onHuntEvent(player);
        assertEquals(0, player.getFood());
        assertEquals(0, player.getPrestigePoints());
    }

    /**
     * SUMMARY:
     * Verifies that HunterBonus grants exactly 1 food and 1 prestige point per hunter for a single hunter.
     *
     * EXPECTATION:
     * Player receives 1 food and 1 prestige point.
     */
    @Test
    void testOneHunterGivesOneFoodAndOnePrestige() {
        player.addCard(new Hunter(12));
        hunterBonus.onHuntEvent(player);
        assertEquals(1, player.getFood());
        assertEquals(1, player.getPrestigePoints());
    }

    /**
     * SUMMARY:
     * Verifies that HunterBonus scales linearly with the number of hunters (3 hunters).
     *
     * EXPECTATION:
     * Player receives 3 food and 3 prestige points (1 per hunter).
     */
    @Test
    void testMultipleHuntersGiveCorrectBonus() {
        player.addCard(new Hunter(12));
        player.addCard(new Hunter(14));
        player.addCard(new Hunter(15));
        hunterBonus.onHuntEvent(player);
        assertEquals(3, player.getFood());
        assertEquals(3, player.getPrestigePoints());
    }

    /**
     * SUMMARY:
     * Verifies that food and prestige from HunterBonus accumulate across multiple Hunt event invocations.
     *
     * EXPECTATION:
     * Two calls with 2 hunters yield 4 food and 4 prestige points total.
     */
    @Test
    void testMultipleCallsAccumulate() {
        player.addCard(new Hunter(12));
        player.addCard(new Hunter(14));

        hunterBonus.onHuntEvent(player);
        hunterBonus.onHuntEvent(player);

        assertEquals(4, player.getFood());
        assertEquals(4, player.getPrestigePoints());
    }
}