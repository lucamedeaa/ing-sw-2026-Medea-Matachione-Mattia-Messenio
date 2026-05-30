package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.DiverseSet;
import it.polimi.ingsw.server.model.card.building.VictoryPoints;
import it.polimi.ingsw.server.model.card.character.*;
import it.polimi.ingsw.server.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DiverseSetTest {

    private DiverseSet diverseSet;
    private Player player;

    @BeforeEach
    void setUp() {
        diverseSet = new DiverseSet(97);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    /**
     * SUMMARY:
     * Verifies that completing a character set before acquiring DiverseSet does not retroactively grant food.
     *
     * EXPECTATION:
     * Player's food is 0 because the set was completed before the building was acquired.
     */
    @Test
    void testNoRewardOnAcquisition() {
        player.addCard(new Hunter(12));
        player.addCard(new Artist(19));
        player.addCard(new Builder(1));
        player.addCard(new Collector(35));
        player.addCard(new Shaman(28));
        player.addCard(new Inventor(39));
        player.addCard(diverseSet);
        assertEquals(0, player.getFood());
    }

    /**
     * SUMMARY:
     * Verifies that completing one full character set after owning DiverseSet grants 5 food.
     *
     * EXPECTATION:
     * Player receives exactly 5 food upon completing one set of all 6 character types.
     */
    @Test
    void testOneNewCompleteSetGivesFiveFood() {
        player.addCard(diverseSet);
        player.addCard(new Hunter(12));
        player.addCard(new Artist(19));
        player.addCard(new Builder(1));
        player.addCard(new Collector(35));
        player.addCard(new Shaman(28));
        player.addCard(new Inventor(39));
        assertEquals(5, player.getFood());
    }

    /**
     * SUMMARY:
     * Verifies that completing two full character sets after owning DiverseSet grants 10 food total.
     *
     * EXPECTATION:
     * Player receives 10 food (5 per completed set × 2 sets).
     */
    @Test
    void testTwoNewCompleteSetsGiveTenFood() {
        player.addCard(diverseSet);
        player.addCard(new Hunter(12));
        player.addCard(new Hunter(14));
        player.addCard(new Artist(19));
        player.addCard(new Artist(20));
        player.addCard(new Builder(1));
        player.addCard(new Builder(2));
        player.addCard(new Collector(35));
        player.addCard(new Collector(36));
        player.addCard(new Shaman(28));
        player.addCard(new Shaman(29));
        player.addCard(new Inventor(39));
        player.addCard(new Inventor(40));
        assertEquals(10, player.getFood());
    }

    /**
     * SUMMARY:
     * Verifies that an incomplete character set (missing one type) does not trigger any food reward.
     *
     * EXPECTATION:
     * Player's food remains 0 when only 5 of 6 character types are present.
     */
    @Test
    void testIncompleteSetGivesNoFood() {
        player.addCard(diverseSet);
        player.addCard(new Hunter(12));
        player.addCard(new Artist(19));
        player.addCard(new Builder(1));
        player.addCard(new Collector(35));
        player.addCard(new Shaman(28));
        assertEquals(0, player.getFood());
    }

    /**
     * SUMMARY:
     * Verifies that building cards do not count toward completing a character set for DiverseSet.
     *
     * EXPECTATION:
     * Adding a VictoryPoints building does not substitute for the missing Inventor; food remains 0.
     */
    @Test
    void testBuildingDoesNotContributeToSet() {
        player.addCard(diverseSet);
        player.addCard(new Hunter(12));
        player.addCard(new Artist(19));
        player.addCard(new Builder(1));
        player.addCard(new Collector(35));
        player.addCard(new Shaman(28));
        player.addCard(new VictoryPoints(109));
        assertEquals(0, player.getFood());
    }

    /**
     * SUMMARY:
     * Verifies that adding a duplicate character type after a complete set does not grant extra food.
     *
     * EXPECTATION:
     * Food stays at 5 after adding a second Hunter (no new complete set formed).
     */
    @Test
    void testNoExtraRewardWithoutNewFullSet() {
        player.addCard(diverseSet);
        player.addCard(new Hunter(12));
        player.addCard(new Artist(19));
        player.addCard(new Builder(1));
        player.addCard(new Collector(35));
        player.addCard(new Shaman(28));
        player.addCard(new Inventor(39));
        assertEquals(5, player.getFood());

        player.addCard(new Hunter(14));
        assertEquals(5, player.getFood());
    }
}