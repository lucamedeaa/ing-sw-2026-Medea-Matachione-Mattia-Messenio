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

    /**
     * SUMMARY:
     * Verifies that executing a Hunt event with 3 hunters grants the correct food and prestige points.
     *
     * EXPECTATION:
     * Player receives 3 food and 3 prestige points from 3 hunters.
     */
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

    /**
     * SUMMARY:
     * Verifies that the HunterBonus building adds extra food and prestige per hunter during a Hunt event.
     *
     * EXPECTATION:
     * With 2 hunters and HunterBonus, player receives 4 food and 4 prestige (2 base + 2 bonus each).
     */
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