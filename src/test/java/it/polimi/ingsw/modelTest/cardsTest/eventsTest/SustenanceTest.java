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

    @Test
    @DisplayName("With insufficient food: food is reset and proportional PP are lost")
    void insufficientFoodAzzerasCiboAndLosesPrestige() {
        Player p = new Player("Frank", TotemColor.ORANGE);
        p.addCard(new Artist(19, 1));
        p.addCard(new Artist(20, 1)); // 2 characters, required food=2

        p.addFood(1); // only 1 food

        // Sustenance Constructor: idcard, era, numPrestRem
        Sustenance s = new Sustenance(61, 1, 2); // 2 PP per unfed character
        s.execute(List.of(p));

        assertEquals(0, p.getFood(), "Food must be reset to zero");
        assertEquals(-2, p.getPrestigePoints(), "Loses 2 PP for 1 unfed character");
    }

    @Test
    @DisplayName("With sufficient food no PP are lost")
    void sufficientFoodNoPPLoss() {
        Player p = new Player("Frank", TotemColor.ORANGE);
        p.addCard(new Artist(19, 1));
        p.addCard(new Artist(20, 1));

        p.addFood(2);

        Sustenance s = new Sustenance(61, 1, 2);
        s.execute(List.of(p));

        assertEquals(0, p.getPrestigePoints());
        assertEquals(0, p.getFood());
    }

    @Test
    @DisplayName("Buildings are not counted in the total to feed")
    void buildingsNotCountedInSustenance() {
        Player p = new Player("Frank", TotemColor.ORANGE);
        p.addCard(new Artist(19, 1)); // 1 character

        // VictoryPoints Constructor: idcard, foodCost, prestigePoints, era
        p.addCard(new VictoryPoints(109, 10, 0, 3)); // building -> does not count

        p.addFood(1);

        Sustenance s = new Sustenance(61, 1, 2);
        s.execute(List.of(p));

        assertEquals(0, p.getPrestigePoints(), "The building must not be counted");
    }

    @Test
    @DisplayName("Zero food and 3 characters: loses 3 * numPP")
    void zeroFoodThreeCharactersMaxLoss() {
        Player p = new Player("Frank", TotemColor.ORANGE);
        p.addCard(new Artist(19, 1));
        p.addCard(new Artist(20, 1));
        p.addCard(new Artist(21, 1));

        Sustenance s = new Sustenance(61, 1, 3); // 3 PP per unfed
        s.execute(List.of(p));

        assertEquals(-9, p.getPrestigePoints());
    }

    @Test
    @DisplayName("Discount greater than total: no food is subtracted")
    void discountExceedsTotalNoFoodTaken() {
        Player p = new Player("Frank", TotemColor.ORANGE);
        // Collector Constructor: idcard, era, discount
        p.addCard(new Collector(35, 1, 3)); // discount 3
        p.addCard(new Collector(36, 1, 3)); // discount 3 -> total discount 6
        p.addCard(new Artist(19, 1));       // 1 character to feed

        p.addFood(5);

        Sustenance s = new Sustenance(61, 1, 2);
        s.execute(List.of(p));

        assertEquals(5, p.getFood(), "With discount >= total no food must be paid");
        assertEquals(0, p.getPrestigePoints());
    }
}