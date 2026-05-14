package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.HunterBonus;
import it.polimi.ingsw.server.model.card.character.Hunter;
import it.polimi.ingsw.server.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HunterBonusTest {

    private HunterBonus hunterBonus;
    private Player player;

    @BeforeEach
    void setUp() {
        hunterBonus = new HunterBonus(108);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    @Test
    void testNoHuntersGivesNothing() {
        hunterBonus.onHuntEvent(player);
        assertEquals(0, player.getFood());
        assertEquals(0, player.getPrestigePoints());
    }

    @Test
    void testOneHunterGivesOneFoodAndOnePrestige() {
        player.addCard(new Hunter(12));
        hunterBonus.onHuntEvent(player);
        assertEquals(1, player.getFood());
        assertEquals(1, player.getPrestigePoints());
    }

    @Test
    void testMultipleHuntersGiveCorrectBonus() {
        player.addCard(new Hunter(12));
        player.addCard(new Hunter(14));
        player.addCard(new Hunter(15));
        hunterBonus.onHuntEvent(player);
        assertEquals(3, player.getFood());
        assertEquals(3, player.getPrestigePoints());
    }

    @Test
    void testMultipleCallsAccumulate() {
        player.addCard(new Hunter(12));
        player.addCard(new Hunter(14));

        hunterBonus.onHuntEvent(player);
        hunterBonus.onHuntEvent(player);

        assertEquals(4, player.getFood());
        assertEquals(4, player.getPrestigePoints());
    }

    @Test
    void testDoesNotCountNonHunters() {
        hunterBonus.onHuntEvent(player);
        assertEquals(0, player.getFood());
        assertEquals(0, player.getPrestigePoints());
    }
}