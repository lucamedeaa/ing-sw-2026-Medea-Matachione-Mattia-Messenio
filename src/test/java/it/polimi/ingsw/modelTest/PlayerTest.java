package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.VictoryPoints;
import it.polimi.ingsw.server.model.card.character.*;
import it.polimi.ingsw.server.model.enums.CharacterType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest extends ModelTest {

    /**
     * SUMMARY:
     * Verifies that a newly created player starts with zero food,
     * zero prestige points, and an empty tribe.
     *
     * EXPECTATION:
     * getFood() and getPrestigePoints() both return 0, and getTribe() is empty.
     */
    @Test
    @DisplayName("Initial food and prestige are zero")
    void initialResourcesAreZero() {
        Player p = newPlayer("Alice");
        assertEquals(0, p.getFood());
        assertEquals(0, p.getPrestigePoints());
        assertTrue(p.getTribe().isEmpty());
    }

    /**
     * SUMMARY:
     * Verifies that addFood() with a positive value correctly increases
     * the player's food supply.
     *
     * EXPECTATION:
     * getFood() returns 5 after adding 5 food.
     */
    @Test
    @DisplayName("addFood increases food correctly")
    void addFoodPositive() {
        Player p = newPlayer("Alice");
        p.addFood(5);
        assertEquals(5, p.getFood());
    }

    /**
     * SUMMARY:
     * Verifies that subtracting food when the player has enough
     * does not cause any prestige penalty.
     *
     * EXPECTATION:
     * Food decreases to 2 and prestige remains 0 after subtracting 3 from 5 food.
     */
    @Test
    @DisplayName("addFood with negative: if food stays >= 0, no prestige loss")
    void addFoodNegativeNoPrestigeLoss() {
        Player p = newPlayer("Alice");
        p.addFood(5);
        p.addFood(-3);
        assertEquals(2, p.getFood());
        assertEquals(0, p.getPrestigePoints());
    }

    /**
     * SUMMARY:
     * Verifies that when food subtraction exceeds current food,
     * the deficit is converted to a prestige penalty (2× the deficit)
     * and food is reset to 0.
     *
     * EXPECTATION:
     * Food becomes 0 and prestige becomes -4 after subtracting 5 from 3 food.
     */
    @Test
    @DisplayName("addFood with negative: if food goes below 0, converts deficit to prestige loss and resets food to 0")
    void addFoodNegativeConvertsToPrestige() {
        Player p = newPlayer("Alice");
        p.addFood(3);
        p.addFood(-5);
        assertEquals(0, p.getFood());
        assertEquals(-4, p.getPrestigePoints());
    }

    /**
     * SUMMARY:
     * Verifies that addPrestige() with a positive value correctly
     * increases the player's prestige points.
     *
     * EXPECTATION:
     * getPrestigePoints() returns 10 after adding 10 prestige.
     */
    @Test
    @DisplayName("addPrestige increases prestige correctly")
    void addPrestigePositive() {
        Player p = newPlayer("Alice");
        p.addPrestige(10);
        assertEquals(10, p.getPrestigePoints());
    }

    /**
     * SUMMARY:
     * Verifies that addPrestige() with a negative value correctly
     * decreases the player's prestige points.
     *
     * EXPECTATION:
     * getPrestigePoints() returns 7 after adding 10 then subtracting 3.
     */
    @Test
    @DisplayName("addPrestige with negative decreases prestige")
    void addPrestigeNegative() {
        Player p = newPlayer("Alice");
        p.addPrestige(10);
        p.addPrestige(-3);
        assertEquals(7, p.getPrestigePoints());
    }

    /**
     * SUMMARY:
     * Verifies that addCard() properly adds character cards to the
     * player's tribe, growing the tribe size with each addition.
     *
     * EXPECTATION:
     * Tribe size is 1 after the first card and 2 after the second card.
     */
    @Test
    @DisplayName("addCard adds card to tribe")
    void addCardIncreaseTribeSize() {
        Player p = newPlayer("Alice");
        give(p, new Artist(19));
        assertEquals(1, p.getTribe().size());
        give(p, new Artist(20));
        assertEquals(2, p.getTribe().size());
    }

    /**
     * SUMMARY:
     * Verifies that countCharactersOfType() accurately counts characters
     * of a specific type within the tribe, returning 0 for absent types.
     *
     * EXPECTATION:
     * Returns 2 for ARTIST, 1 for HUNTER, and 0 for SHAMAN.
     */
    @Test
    @DisplayName("countCharactersOfType counts correctly")
    void countCharactersOfType() {
        Player p = newPlayer("Alice");
        give(p, new Artist(19));
        give(p, new Artist(20));
        give(p, new Hunter(10));
        assertEquals(2, p.countCharactersOfType(CharacterType.ARTIST));
        assertEquals(1, p.countCharactersOfType(CharacterType.HUNTER));
        assertEquals(0, p.countCharactersOfType(CharacterType.SHAMAN));
    }

    /**
     * SUMMARY:
     * Verifies that getFoodDiscount() returns the cumulative food discount
     * contributed by Builder cards in the player's tribe.
     *
     * EXPECTATION:
     * getFoodDiscount() returns 3 for two Builders with discounts of 1 and 2.
     */
    @Test
    @DisplayName("getFoodDiscount returns sum of discounts from tribe")
    void getFoodDiscount() {
        Player p = newPlayer("Alice");
        give(p, new Builder(1)); // Sconto 1
        give(p, new Builder(2)); // Sconto 2
        assertEquals(3, p.getFoodDiscount());
    }

    /**
     * SUMMARY:
     * Verifies that getStarsNumber() returns the cumulative number of
     * stars contributed by Shaman cards in the player's tribe.
     *
     * EXPECTATION:
     * getStarsNumber() returns 3 for two Shamans with 1 and 2 stars respectively.
     */
    @Test
    @DisplayName("getStarsNumber returns sum of stars from tribe")
    void getStarsNumber() {
        Player p = newPlayer("Alice");
        give(p, new Shaman(28)); // 1 stella
        give(p, new Shaman(29)); // 2 stelle
        assertEquals(3, p.getStarsNumber());
    }

    /**
     * SUMMARY:
     * Verifies that adding Hunters with the food-granting icon awards
     * food equal to the number of hunters already in the tribe at the
     * time each hunter is added.
     *
     * EXPECTATION:
     * Total food is 3 after adding two icon-bearing Hunters (0+1 from first, +2 from second).
     */
    @Test
    @DisplayName("Hunter with icon grants food equal to hunters already in tribe")
    void hunterWithIconGrantsFood() {
        Player p = newPlayer("Alice");
        give(p, new Hunter(10)); // +1
        give(p, new Hunter(11)); // +2
        assertEquals(3, p.getFood());
    }

    /**
     * SUMMARY:
     * Verifies that Hunters without the food-granting icon do not
     * award any food when added to the tribe.
     *
     * EXPECTATION:
     * Food remains 0 after adding two Hunters without the food icon.
     */
    @Test
    @DisplayName("Hunter without icon grants no food")
    void hunterWithoutIconGrantsNoFood() {
        Player p = newPlayer("Alice");
        give(p, new Hunter(12));
        give(p, new Hunter(14));
        assertEquals(0, p.getFood());
    }
}