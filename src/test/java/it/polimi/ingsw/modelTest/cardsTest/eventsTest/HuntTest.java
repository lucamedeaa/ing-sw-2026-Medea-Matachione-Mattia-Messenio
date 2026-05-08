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
        // Hunter Constructor: idcard, era, hasIcon
        p.addCard(new Hunter(10, 1, false));
        p.addCard(new Hunter(11, 1, false));
        p.addCard(new Hunter(12, 1, false));

        // Hunt Constructor: idcard, era, foodGiven, prestigeGiven
        Hunt h = new Hunt(56, 1, 1, 2); // 1 food and 2 PP per hunter
        h.execute(List.of(p));

        assertEquals(3, p.getFood());
        assertEquals(6, p.getPrestigePoints());
    }

    @Test
    @DisplayName("HunterBonus adds extra food and PP per hunter during Hunt")
    void hunterBonusBuildingAddsExtraFoodAndPrestige() {
        Player p = new Player("Gina", TotemColor.ORANGE);
        p.addCard(new Hunter(10, 1, false));
        p.addCard(new Hunter(11, 1, false));

        // HunterBonus Constructor: idcard, foodCost, prestigePoints, era
        p.addCard(new HunterBonus(108, 7, 2, 2));

        Hunt h = new Hunt(56, 1, 1, 1); // 1 food, 1 PP per hunter
        h.execute(List.of(p));

        // Base Hunt: 2 hunters * (1 food, 1 PP) = 2 food, 2 PP
        // HunterBonus: 2 hunters * (1 food, 1 PP) = 2 food, 2 PP
        // Total = 4 food, 4 PP (assuming HunterBonus gives 1 food, 1 PP per hunter natively)
        assertEquals(4, p.getFood());
        assertEquals(4, p.getPrestigePoints());
    }
}