package it.polimi.ingsw.modelTest.cardsTest.eventsTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.VictoryPoints;
import it.polimi.ingsw.server.model.card.character.Artist;
import it.polimi.ingsw.server.model.card.character.Collector;
import it.polimi.ingsw.server.model.card.event.Sustenance;
import it.polimi.ingsw.server.model.enums.TotemColor;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SustenanceTest extends ModelTest {

    /**
     * SUMMARY:
     * Verifies that when a player has insufficient food, food is reset to zero and prestige is lost proportionally.
     *
     * EXPECTATION:
     * Food becomes 0 and the player loses 1 PP for 1 unfed character card.
     */
    @Test
    @DisplayName("With insufficient food: food is reset and proportional PP are lost")
    void insufficientFoodResetsFoodAndLosesPrestige() {
        Player p = new Player("Frank", TotemColor.ORANGE);
        p.addCard(new Artist(19));
        p.addCard(new Artist(20));

        p.addFood(1);

        Sustenance s = new Sustenance(61);
        s.execute(List.of(p));

        assertEquals(0, p.getFood(), "Food must be reset to zero");
        assertEquals(-1, p.getPrestigePoints(), "Loses 1 PP for 1 unfed character");
    }

    /**
     * SUMMARY:
     * Verifies that when a player has exactly enough food for all character cards, no prestige is lost.
     *
     * EXPECTATION:
     * Prestige remains 0 and food is fully consumed.
     */
    @Test
    @DisplayName("With sufficient food no PP are lost")
    void sufficientFoodNoPPLoss() {
        Player p = new Player("Frank", TotemColor.ORANGE);
        p.addCard(new Artist(19));
        p.addCard(new Artist(20));
        p.addFood(2);

        Sustenance s = new Sustenance(61);
        s.execute(List.of(p));

        assertEquals(0, p.getPrestigePoints());
        assertEquals(0, p.getFood());
    }

    /**
     * SUMMARY:
     * Verifies that building cards are excluded from the character count when calculating feeding cost.
     *
     * EXPECTATION:
     * Only the Artist counts toward feeding; 1 food covers it, so no PP are lost.
     */
    @Test
    @DisplayName("Buildings are not counted in the total to feed")
    void buildingsNotCountedInSustenance() {
        Player p = new Player("Frank", TotemColor.ORANGE);
        p.addCard(new Artist(19));
        p.addCard(new VictoryPoints(109));
        p.addFood(1);

        Sustenance s = new Sustenance(61);
        s.execute(List.of(p));

        assertEquals(0, p.getPrestigePoints(), "The building must not be counted");
    }

    /**
     * SUMMARY:
     * Verifies that having zero food with 3 character cards results in the maximum prestige loss.
     *
     * EXPECTATION:
     * The player loses 3 PP, one for each unfed character card.
     */
    @Test
    @DisplayName("Zero food and 3 characters: loses 3 * numPP")
    void zeroFoodThreeCharactersMaxLoss() {
        Player p = new Player("Frank", TotemColor.ORANGE);
        p.addCard(new Artist(19));
        p.addCard(new Artist(20));
        p.addCard(new Artist(21));

        Sustenance s = new Sustenance(61);
        s.execute(List.of(p));

        assertEquals(-3, p.getPrestigePoints());
    }

    /**
     * SUMMARY:
     * Verifies that when the Collector discount exceeds the total feeding cost, no food is subtracted.
     *
     * EXPECTATION:
     * Player's food remains at 5 and no prestige is lost.
     */
    @Test
    @DisplayName("Discount greater than total: no food is subtracted")
    void discountExceedsTotalNoFoodTaken() {
        Player p = new Player("Frank", TotemColor.ORANGE);
        p.addCard(new Collector(35));
        p.addCard(new Collector(36));
        p.addCard(new Artist(19));

        p.addFood(5);

        Sustenance s = new Sustenance(61);
        s.execute(List.of(p));

        assertEquals(5, p.getFood(), "With discount >= total no food must be paid");
        assertEquals(0, p.getPrestigePoints());
    }
}