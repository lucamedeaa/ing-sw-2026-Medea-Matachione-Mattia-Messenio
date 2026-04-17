package it.polimi.ingsw.modelTest.cardsTest.eventsTest;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.buildings.HunterBonus;
import it.polimi.ingsw.model.cards.drawableCards.characters.Hunter;
import it.polimi.ingsw.model.cards.events.Hunt;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HuntTest extends ModelTest {
    /**
     * With 3 hunters: +3 food and +3×PP for each hunter.
     */
    @Test
    @DisplayName("Hunt with 3 hunters: correct food and PP")
    void huntWithThreeHunters() {
        Player p = newPlayer("Gina");
        give(p, new Hunter(1, false));
        give(p, new Hunter(1, false));
        give(p, new Hunter(1, false));

        Hunt h = new Hunt(1, 1, 2); // 1 food and 2 PP per hunter
        h.execute(List.of(p));

        assertEquals(3, p.getFood());
        assertEquals(6, p.getPrestigePoints());
    }

    /**
     * HunterBonus: for each hunter adds 1 food and 1 PP extra.
     * With 2 hunters and HunterBonus: Hunt gives 2 base food + 2 from building = 4.
     */
    @Test
    @DisplayName("HunterBonus adds 1 food and 1 PP per hunter during Hunt")
    void hunterBonusBuildingAddsExtraFoodAndPrestige() {
        Player p = newPlayer("Gina");
        give(p, new Hunter(1, false));
        give(p, new Hunter(1, false));
        give(p, new HunterBonus(0, 0, 2)); // free building for the test

        Hunt h = new Hunt(1, 1, 1); // 1 food, 1 PP per hunter
        h.execute(List.of(p));

        // Base: 2 food + 2 PP; HunterBonus: +2 food +2 PP
        assertEquals(4, p.getFood());
        assertEquals(4, p.getPrestigePoints());
    }
}
