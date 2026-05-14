package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.RitualStars;
import it.polimi.ingsw.server.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RitualStarsTest {

    private RitualStars ritualStars;
    private Player player;

    @BeforeEach
    void setUp() {
        // Updated Constructor: idcard, foodCost, prestigePoints, era
        ritualStars = new RitualStars(104);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    @Test
    void testReturnsThree() {
        int result = ritualStars.onShamanicRitualEvent(player, 0, 0);

        assertEquals(3, result,
                "RitualStars deve sempre restituire 3");
    }

    @Test
    void testReturnsThreeRegardlessOfIncrement() {
        int result = ritualStars.onShamanicRitualEvent(player, 10, 0);

        assertEquals(3, result,
                "Il valore restituito non dipende da increment");
    }

    @Test
    void testReturnsThreeRegardlessOfDecrement() {
        int result = ritualStars.onShamanicRitualEvent(player, 0, -5);

        assertEquals(3, result,
                "Il valore restituito non dipende da decrement");
    }

    @Test
    void testReturnsThreeAlways() {
        int result1 = ritualStars.onShamanicRitualEvent(player, 0, 0);
        int result2 = ritualStars.onShamanicRitualEvent(player, 5, -3);

        assertEquals(3, result1);
        assertEquals(3, result2,
                "Deve sempre restituire 3 indipendentemente dai parametri");
    }

    @Test
    void testDoesNotModifyPlayer() {
        ritualStars.onShamanicRitualEvent(player, 10, -5);

        assertEquals(0, player.getPrestigePoints(),
                "RitualStars non deve modificare il prestigio del player");
        assertEquals(0, player.getFood(),
                "RitualStars non deve modificare il cibo del player");
    }
}