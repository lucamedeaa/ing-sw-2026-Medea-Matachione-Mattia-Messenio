package it.polimi.ingsw.modelTest.cardsTest.eventsTest;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.buildings.VictoryPoints;
import it.polimi.ingsw.model.cards.drawableCards.characters.Artist;
import it.polimi.ingsw.model.cards.drawableCards.characters.Collector;
import it.polimi.ingsw.model.cards.events.Sustenance;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SustenanceTest extends ModelTest {
    /**
     * available food MUST be paid before losing PP.
     * You cannot choose to lose PP to keep food.
     * With 2 characters and 1 food: pays 1, loses 1×PP for 1 unfed
     */
    @Test
    @DisplayName("With insufficient food: food is reset and proportional PP are lost")
    void insufficientFoodAzzerasCiboAndLosesPrestige() {
        Player p = newPlayer("Frank");
        give(p, new Artist(1));
        give(p, new Artist(1)); // 2 characters, required food=2
        p.addFood(1); // only 1 food

        Sustenance s = new Sustenance(1, 2); // 2 PP per unfed character
        s.execute(List.of(p));

        assertEquals(0, p.getFood(), "Food must be reset to zero");
        assertEquals(-2, p.getPrestigePoints(), "Loses 2 PP for 1 unfed character");
    }

    /**
     * With sufficient food: pays exactly the number of characters, no PP lost.
     */
    @Test
    @DisplayName("With sufficient food no PP are lost")
    void sufficientFoodNoPPLoss() {
        Player p = newPlayer("Frank");
        give(p, new Artist(1));
        give(p, new Artist(1));
        p.addFood(2);

        Sustenance s = new Sustenance(1, 2);
        s.execute(List.of(p));

        assertEquals(0, p.getPrestigePoints());
        assertEquals(0, p.getFood());
    }

    /**
     * Buildings DO NOT count as characters to feed.
     */
    @Test
    @DisplayName("Buildings are not counted in the total to feed")
    void buildingsNotCountedInSustenance() {
        Player p = newPlayer("Frank");
        give(p, new Artist(1));          // 1 character → total=1
        give(p, new VictoryPoints(0, 0, 3)); // building → does not count
        p.addFood(1);

        Sustenance s = new Sustenance(1, 2);
        s.execute(List.of(p));

        assertEquals(0, p.getPrestigePoints(), "The building must not be counted");
    }

    /**
     * With food=0 and 3 unfed characters: loses 3×PP.
     */
    @Test
    @DisplayName("Zero food and 3 characters: loses 3×numPP")
    void zeroFoodThreeCharactersMaxLoss() {
        Player p = newPlayer("Frank");
        give(p, new Artist(1));
        give(p, new Artist(1));
        give(p, new Artist(1));

        Sustenance s = new Sustenance(1, 3); // 3 PP per unfed
        s.execute(List.of(p));

        assertEquals(-9, p.getPrestigePoints());
    }


    @Test
    @DisplayName("Discount greater than total: no food is subtracted")
    void discountExceedsTotalNoFoodTaken() {
        Player p = newPlayer("Frank");
        give(p, new Collector(1, 3)); // discount 3
        give(p, new Collector(1, 3)); // discount 3 → total discount 6
        give(p, new Artist(1));       // 1 character to feed
        p.addFood(5);

        Sustenance s = new Sustenance(1, 2);
        s.execute(List.of(p));

        // discount=6, total=1 → total <= discount
        // food is NOT subtracted → food remains 5
        assertEquals(5, p.getFood(), "With discount >= total no food must be paid");
        assertEquals(0, p.getPrestigePoints());
    }
}
