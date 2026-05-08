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
        hunterBonus = new HunterBonus(108, 7, 2, 2);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    @Test
    void testNoHuntersGivesNothing() {
        hunterBonus.onHuntEvent(player);

        assertEquals(0, player.getFood(), "Senza hunter non deve dare cibo");
        assertEquals(0, player.getPrestigePoints(), "Senza hunter non deve dare prestigio");
    }

    @Test
    void testOneHunterGivesOneFoodAndOnePrestige() {
        // hasIcon deve essere FALSE per non inquinare il cibo iniziale
        player.addCard(new Hunter(10, 1, false));

        hunterBonus.onHuntEvent(player);

        assertEquals(1, player.getFood(), "1 hunter = +1 cibo");
        assertEquals(1, player.getPrestigePoints(), "1 hunter = +1 prestigio");
    }

    @Test
    void testMultipleHuntersGiveCorrectBonus() {
        player.addCard(new Hunter(10, 1, false));
        player.addCard(new Hunter(11, 1, false));
        player.addCard(new Hunter(12, 1, false));

        hunterBonus.onHuntEvent(player);

        assertEquals(3, player.getFood(), "3 hunter = +3 cibo");
        assertEquals(3, player.getPrestigePoints(), "3 hunter = +3 prestigio");
    }

    @Test
    void testMultipleCallsAccumulate() {
        player.addCard(new Hunter(10, 1, false));
        player.addCard(new Hunter(11, 1, false));

        hunterBonus.onHuntEvent(player);
        hunterBonus.onHuntEvent(player);

        assertEquals(4, player.getFood(), "2 hunter * 2 chiamate = 4 cibo");
        assertEquals(4, player.getPrestigePoints(), "2 hunter * 2 chiamate = 4 prestigio");
    }

    @Test
    void testDoesNotCountNonHunters() {
        hunterBonus.onHuntEvent(player);

        assertEquals(0, player.getFood());
        assertEquals(0, player.getPrestigePoints());
    }
}