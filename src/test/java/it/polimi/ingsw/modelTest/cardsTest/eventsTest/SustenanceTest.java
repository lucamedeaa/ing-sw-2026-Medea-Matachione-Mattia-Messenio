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
        p.addCard(new Artist(19));
        p.addCard(new Artist(20));

        p.addFood(1);

        Sustenance s = new Sustenance(61);
        s.execute(List.of(p));

        assertEquals(0, p.getFood(), "Food must be reset to zero");
        assertEquals(-1, p.getPrestigePoints(), "Loses 1 PP for 1 unfed character");
    }

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