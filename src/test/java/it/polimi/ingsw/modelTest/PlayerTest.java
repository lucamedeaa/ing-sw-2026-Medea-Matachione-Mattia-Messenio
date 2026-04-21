package it.polimi.ingsw.modelTest;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.buildings.VictoryPoints;
import it.polimi.ingsw.model.cards.drawableCards.characters.*;
import it.polimi.ingsw.model.enums.CharacterType;
import it.polimi.ingsw.model.enums.InventorIcon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest extends ModelTest {

    @Test
    @DisplayName("addFood below zero: food remains 0, loses 2PP for food in deficit")
    void addFoodBelowZeroCurrentBehavior() {
        Player p = newPlayer("Mario");
        p.addFood(1);
        p.addFood(-3); // goes to -2 → food=0, loses 2*2=4 PP? No: loses 2*(-2)=-4 PP
        // food goes to 1-3 = -2 → addPrestige(2*(-2)) = addPrestige(-4)
        assertEquals(0, p.getFood());
        assertEquals(-4, p.getPrestigePoints(),
                "Logic Error");
    }

    /**
     * calculateTotalScore includes PP from prestige + cards finalPoints + artists + inventors.
     */
    @Test
    @DisplayName("calculateTotalScore correctly sums all components")
    void calculateTotalScoreAllComponents() {
        Player p = newPlayer("Nina");
        p.addPrestige(10); // PP accumulated during the game
        give(p, new Artist(1));
        give(p, new Artist(1)); // 10 PP (1 pair)
        give(p, new Inventor(1, InventorIcon.CANOE)); // 1 inventor × 1 icon = 1 PP
        give(p, new VictoryPoints(0, 5, 3)); // 25 + 5 = 30 PP

        int total = p.calculateTotalScore();
        // 10 (prestige) + 30 (VictoryPoints) + 10 (1 pair of artists) + 1 (inventors) = 51
        assertEquals(51, total);
    }

    /**
     * countCharactersOfType does not count Buildings as characters.
     */
    @Test
    @DisplayName("countCharactersOfType does not include buildings")
    void countCharactersExcludesBuildings() {
        Player p = newPlayer("Otto");
        give(p, new Artist(1));
        give(p, new VictoryPoints(0, 0, 3));
        give(p, new Hunter(1, false));

        assertEquals(1, p.countCharactersOfType(CharacterType.ARTIST));
        assertEquals(1, p.countCharactersOfType(CharacterType.HUNTER));
        assertEquals(0, p.countCharactersOfType(CharacterType.BUILDER));
    }

    /**
     * getFoodDiscount sums the discounts of all Builders and Collectors.
     */
    @Test
    @DisplayName("getFoodDiscount sums all discounts present in the tribe")
    void foodDiscountIsSumOfAllDiscounts() {
        Player p = newPlayer("Otto");
        give(p, new Builder(1, 2, 0)); // discount 2
        give(p, new Builder(1, 1, 0)); // discount 1
        give(p, new Collector(1, 3)); // discount 3 (but only for Sustenance)

        // getFoodDiscount uses Card::getFoodDiscount which in Builder returns foodDiscount
        // Collector overrides getFoodDiscount() with its discount
        assertEquals(6, p.getFoodDiscount());
    }
}
