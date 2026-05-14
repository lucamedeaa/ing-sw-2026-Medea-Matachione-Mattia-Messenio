package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.VictoryPoints;
import it.polimi.ingsw.server.model.card.character.*;
import it.polimi.ingsw.server.model.enums.CharacterType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest extends ModelTest {

    @Test
    @DisplayName("getNickname returns the name given at construction")
    void nicknameIsCorrect() {
        assertEquals("Alice", newPlayer("Alice").getNickname());
    }

    @Test
    @DisplayName("Initial food and prestige are zero")
    void initialResourcesAreZero() {
        Player p = newPlayer("Alice");
        assertEquals(0, p.getFood());
        assertEquals(0, p.getPrestigePoints());
        assertTrue(p.getTribe().isEmpty());
    }

    @Test
    @DisplayName("addFood increases food correctly")
    void addFoodPositive() {
        Player p = newPlayer("Alice");
        p.addFood(5);
        assertEquals(5, p.getFood());
    }

    @Test
    @DisplayName("addFood(0) leaves food unchanged")
    void addFoodZero() {
        Player p = newPlayer("Alice");
        p.addFood(3);
        p.addFood(0);
        assertEquals(3, p.getFood());
    }

    @Test
    @DisplayName("addFood with negative: if food stays >= 0, no prestige loss")
    void addFoodNegativeNoPrestigeLoss() {
        Player p = newPlayer("Alice");
        p.addFood(5);
        p.addFood(-3);
        assertEquals(2, p.getFood());
        assertEquals(0, p.getPrestigePoints());
    }

    @Test
    @DisplayName("addFood with negative: if food goes below 0, converts deficit to prestige loss and resets food to 0")
    void addFoodNegativeConvertsToPrestige() {
        Player p = newPlayer("Alice");
        p.addFood(3);
        p.addFood(-5);
        assertEquals(0, p.getFood());
        assertEquals(-4, p.getPrestigePoints());
    }

    @Test
    @DisplayName("addPrestige increases prestige correctly")
    void addPrestigePositive() {
        Player p = newPlayer("Alice");
        p.addPrestige(10);
        assertEquals(10, p.getPrestigePoints());
    }

    @Test
    @DisplayName("addPrestige with negative decreases prestige")
    void addPrestigeNegative() {
        Player p = newPlayer("Alice");
        p.addPrestige(10);
        p.addPrestige(-3);
        assertEquals(7, p.getPrestigePoints());
    }

    @Test
    @DisplayName("addCard adds card to tribe")
    void addCardIncreaseTribeSize() {
        Player p = newPlayer("Alice");
        give(p, new Artist(19));
        assertEquals(1, p.getTribe().size());
        give(p, new Artist(20));
        assertEquals(2, p.getTribe().size());
    }

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

    @Test
    @DisplayName("getFoodDiscount returns sum of discounts from tribe")
    void getFoodDiscount() {
        Player p = newPlayer("Alice");
        give(p, new Builder(1)); // Sconto 1
        give(p, new Builder(2)); // Sconto 2
        assertEquals(3, p.getFoodDiscount());
    }

    @Test
    @DisplayName("getFoodDiscount is 0 with no discount cards")
    void getFoodDiscountZero() {
        Player p = newPlayer("Alice");
        give(p, new Artist(19));
        assertEquals(0, p.getFoodDiscount());
    }

    @Test
    @DisplayName("getStarsNumber returns sum of stars from tribe")
    void getStarsNumber() {
        Player p = newPlayer("Alice");
        give(p, new Shaman(28)); // 1 stella
        give(p, new Shaman(29)); // 2 stelle
        assertEquals(3, p.getStarsNumber());
    }

    @Test
    @DisplayName("Hunter with icon grants food equal to hunters already in tribe")
    void hunterWithIconGrantsFood() {
        Player p = newPlayer("Alice");
        give(p, new Hunter(10)); // +1
        give(p, new Hunter(11)); // +2
        assertEquals(3, p.getFood());
    }

    @Test
    @DisplayName("Hunter without icon grants no food")
    void hunterWithoutIconGrantsNoFood() {
        Player p = newPlayer("Alice");
        give(p, new Hunter(12));
        give(p, new Hunter(14));
        assertEquals(0, p.getFood());
    }

    @Test
    @DisplayName("calculateTotalScore with only prestige points")
    void totalScoreOnlyPrestige() {
        Player p = newPlayer("Alice");
        p.addPrestige(15);
        assertEquals(15, p.calculateTotalScore());
    }

    @Test
    @DisplayName("1 artist contributes no bonus (bonus requires pairs)")
    void oneArtistNoBonus() {
        Player p = newPlayer("Alice");
        give(p, new Artist(19));
        assertEquals(0, p.calculateTotalScore());
    }

    @Test
    @DisplayName("2 artists contribute 10 points")
    void twoArtistsGiveTenPoints() {
        Player p = newPlayer("Alice");
        give(p, new Artist(19));
        give(p, new Artist(20));
        assertEquals(10, p.calculateTotalScore());
    }

    @Test
    @DisplayName("4 artists contribute 20 points")
    void fourArtistsGiveTwentyPoints() {
        Player p = newPlayer("Alice");
        give(p, new Artist(19));
        give(p, new Artist(20));
        give(p, new Artist(21));
        give(p, new Artist(22));
        assertEquals(20, p.calculateTotalScore());
    }

    @Test
    @DisplayName("VictoryPoints building adds prestigePoints at end game")
    void victoryPointsBuildingScore() {
        Player p = newPlayer("Alice");
        give(p, new VictoryPoints(109));
        assertEquals(25, p.calculateTotalScore());
    }

    @Test
    @DisplayName("Builder contributes endGamePrestigePoints to total score")
    void builderFinalPoints() {
        Player p = newPlayer("Alice");
        give(p, new Builder(1));
        assertEquals(2, p.calculateTotalScore());
    }

    @Test
    @DisplayName("calculateTotalScore combines prestige, artists and buildings")
    void totalScoreCombined() {
        Player p = newPlayer("Alice");
        p.addPrestige(5);
        give(p, new Artist(19));
        give(p, new Artist(20)); // +10
        give(p, new VictoryPoints(109)); // +25
        assertEquals(40, p.calculateTotalScore());
    }
}