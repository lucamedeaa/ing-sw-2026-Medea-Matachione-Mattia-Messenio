package it.polimi.ingsw.modelTest.cardsTest.eventsTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.HunterBonus;
import it.polimi.ingsw.server.model.card.character.Hunter;
import it.polimi.ingsw.server.model.card.event.Hunt;
import it.polimi.ingsw.server.model.enums.TotemColor;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HuntTest extends ModelTest {

    @Test
    @DisplayName("Hunt with 3 hunters: correct food and PP")
    void huntWithThreeHunters() {
        Player p = new Player("Gina", TotemColor.ORANGE);
        p.addCard(new Hunter(12));
        p.addCard(new Hunter(14));
        p.addCard(new Hunter(15));

        Hunt h = new Hunt(56);
        h.execute(List.of(p));

        assertEquals(3, p.getFood());
        assertEquals(3, p.getPrestigePoints()); // Era 1 -> 3
    }

    @Test
    @DisplayName("HunterBonus adds extra food and PP per hunter during Hunt")
    void hunterBonusBuildingAddsExtraFoodAndPrestige() {
        Player p = new Player("Gina", TotemColor.ORANGE);
        p.addCard(new Hunter(12));
        p.addCard(new Hunter(14));

        p.addCard(new HunterBonus(108));

        Hunt h = new Hunt(56);
        h.execute(List.of(p));

        assertEquals(4, p.getFood());
        assertEquals(4, p.getPrestigePoints());
    }
}