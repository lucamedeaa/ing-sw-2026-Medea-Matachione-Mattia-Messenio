package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.buildings.DoublePrestigeShaman;
import it.polimi.ingsw.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DoublePrestigeShamanTest {

    private DoublePrestigeShaman doublePrestigeShaman;
    private Player player;

    @BeforeEach
    void setUp() {
        doublePrestigeShaman = new DoublePrestigeShaman(0, 0, 1);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    @Test
    void testPositiveIncrementAddsPrestigeAgain() {
        int result = doublePrestigeShaman.onShamanicRitualEvent(player, 10, -5);

        assertEquals(10, player.getPrestigePoints(),
                "Con increment positivo deve aggiungere nuovamente quei punti prestigio");
        assertEquals(0, result, "Il metodo deve restituire sempre 0");
    }

    @Test
    void testZeroIncrementAddsNothing() {
        int result = doublePrestigeShaman.onShamanicRitualEvent(player, 0, -5);

        assertEquals(0, player.getPrestigePoints(),
                "Con increment uguale a 0 non deve aggiungere prestigio");
        assertEquals(0, result, "Il metodo deve restituire sempre 0");
    }

    @Test
    void testDecrementIsIgnored() {
        int result = doublePrestigeShaman.onShamanicRitualEvent(player, 0, -10);

        assertEquals(0, player.getPrestigePoints(),
                "Il decrement non deve avere effetto in questa carta");
        assertEquals(0, result, "Il metodo deve restituire sempre 0");
    }

    @Test
    void testMultipleCallsAccumulatePrestige() {
        doublePrestigeShaman.onShamanicRitualEvent(player, 5, -2);
        doublePrestigeShaman.onShamanicRitualEvent(player, 5, -2);

        assertEquals(10, player.getPrestigePoints(),
                "Chiamate multiple con increment positivo devono accumularsi");
    }
}