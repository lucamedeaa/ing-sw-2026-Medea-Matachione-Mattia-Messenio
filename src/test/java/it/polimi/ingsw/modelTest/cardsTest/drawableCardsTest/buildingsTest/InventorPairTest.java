package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.InventorPair;
import it.polimi.ingsw.server.model.card.character.Inventor;
import it.polimi.ingsw.server.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InventorPairTest {

    private InventorPair inventorPair;
    private Player player;

    @BeforeEach
    void setUp() {
        inventorPair = new InventorPair(98);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    /**
     * SUMMARY:
     * Verifies that a pre-existing inventor pair does not retroactively grant food when InventorPair is acquired.
     *
     * EXPECTATION:
     * Player's food is 0 because the pair existed before the building was added.
     */
    @Test
    void testNoRewardOnAcquisitionWithPreExistingPair() {
        player.addCard(new Inventor(39)); // SPEARHEAD
        player.addCard(new Inventor(72)); // SPEARHEAD
        player.addCard(inventorPair);
        assertEquals(0, player.getFood());
    }

    /**
     * SUMMARY:
     * Verifies that adding a single inventor after owning InventorPair does not grant food (no pair formed yet).
     *
     * EXPECTATION:
     * Player's food remains 0 with only one inventor.
     */
    @Test
    void testSingleInventorAfterAcquisitionGivesNoFood() {
        player.addCard(inventorPair);
        player.addCard(new Inventor(39));
        assertEquals(0, player.getFood());
    }

    /**
     * SUMMARY:
     * Verifies that forming a new inventor pair (same icon) after owning InventorPair grants 3 food.
     *
     * EXPECTATION:
     * Player receives exactly 3 food when two inventors with matching icons are added.
     */
    @Test
    void testNewPairAfterAcquisitionGivesThreeFood() {
        player.addCard(inventorPair);
        player.addCard(new Inventor(39)); // SPEARHEAD
        player.addCard(new Inventor(72)); // SPEARHEAD
        assertEquals(3, player.getFood());
    }

    /**
     * SUMMARY:
     * Verifies that two inventors with different icons do not form a pair and grant no food.
     *
     * EXPECTATION:
     * Player's food remains 0 when inventors have mismatched icons.
     */
    @Test
    void testDifferentIconsDoNotFormPair() {
        player.addCard(inventorPair);
        player.addCard(new Inventor(39)); // SPEARHEAD
        player.addCard(new Inventor(41)); // BREAD
        assertEquals(0, player.getFood());
    }

    /**
     * SUMMARY:
     * Verifies that forming two distinct inventor pairs grants 6 food total (3 per pair).
     *
     * EXPECTATION:
     * Player receives 6 food from two separate icon-matching pairs.
     */
    @Test
    void testTwoNewPairsGiveSixFood() {
        player.addCard(inventorPair);
        player.addCard(new Inventor(39));
        player.addCard(new Inventor(72)); // +3
        player.addCard(new Inventor(41)); // BREAD
        player.addCard(new Inventor(52)); // BREAD (+3)
        assertEquals(6, player.getFood());
    }

    /**
     * SUMMARY:
     * Verifies that a pre-existing single inventor can pair with a newly added matching inventor to grant food.
     *
     * EXPECTATION:
     * Player receives 3 food when the second matching inventor completes the pair after building acquisition.
     */
    @Test
    void testPreExistingSingleInventorAndOneNewMatchingInventorGiveThreeFood() {
        player.addCard(new Inventor(39));
        player.addCard(inventorPair);
        player.addCard(new Inventor(72));
        assertEquals(3, player.getFood());
    }

    /**
     * SUMMARY:
     * Verifies that a third inventor with the same icon does not form an additional pair.
     *
     * EXPECTATION:
     * Food stays at 3; only one pair per icon is rewarded.
     */
    @Test
    void testThirdInventorOfSameIconDoesNotGiveMoreFood() {
        player.addCard(inventorPair);
        player.addCard(new Inventor(39));
        player.addCard(new Inventor(72)); // +3
        player.addCard(new Inventor(72)); // No new pair
        assertEquals(3, player.getFood());
    }
}