package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.BuilderMastery;
import it.polimi.ingsw.server.model.card.character.Builder;
import it.polimi.ingsw.server.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BuilderMasteryTest {

    private BuilderMastery builderMastery;
    private Player player;

    @BeforeEach
    void setUp() {
        builderMastery = new BuilderMastery(103);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    /**
     * SUMMARY:
     * Verifies that BuilderMastery returns only its base prestige points when the player has no Builder cards.
     *
     * EXPECTATION:
     * getFinalPoints returns 4 (base prestige only).
     */
    @Test
    void testNoBuilders() {
        player.addCard(builderMastery);
        assertEquals(4, builderMastery.getFinalPoints(player),
                "Senza builder deve restituire solo i prestigePoints dell'edificio");
    }

    /**
     * SUMMARY:
     * Verifies that BuilderMastery adds a single Builder's prestige points to its own base prestige.
     *
     * EXPECTATION:
     * getFinalPoints returns 6 (Builder 2 PP + base 4 PP).
     */
    @Test
    void testSingleBuilder() {
        player.addCard(new Builder(1));
        player.addCard(builderMastery);

        assertEquals(6, builderMastery.getFinalPoints(player),
                "1 builder (2 PP) + prestige (4) = 6");
    }

    /**
     * SUMMARY:
     * Verifies that BuilderMastery sums the prestige points from multiple Builders with its own base.
     *
     * EXPECTATION:
     * getFinalPoints returns the sum of all Builders' PP plus base prestige.
     */
    @Test
    void testMultipleBuilders() {
        player.addCard(new Builder(1));
        player.addCard(new Builder(2));
        player.addCard(builderMastery);

        // (2 + 0) + 4 = 6
        assertEquals(6, builderMastery.getFinalPoints(player),
                "Somma builder + prestigePoints");
    }

    /**
     * SUMMARY:
     * Verifies that BuilderMastery only counts Builder character cards, not other building cards.
     *
     * EXPECTATION:
     * Non-Builder cards are ignored; result is Builder PP + base prestige only.
     */
    @Test
    void testOnlyCountsBuilders() {
        player.addCard(new Builder(1));
        player.addCard(new BuilderMastery(103)); // Another building
        player.addCard(builderMastery);

        // Builder PP (2) + BuilderMastery PP (4) = 6
        assertEquals(6, builderMastery.getFinalPoints(player),
                "Deve contare solo i Builder");
    }

    /**
     * SUMMARY:
     * Verifies that getFinalPoints is idempotent and returns consistent results across multiple calls.
     *
     * EXPECTATION:
     * Two consecutive calls return the same value.
     */
    @Test
    void testMultipleCallsConsistency() {
        player.addCard(new Builder(1));
        player.addCard(builderMastery);

        int first = builderMastery.getFinalPoints(player);
        int second = builderMastery.getFinalPoints(player);

        assertEquals(first, second, "Chiamate multiple devono dare lo stesso risultato");
    }
}