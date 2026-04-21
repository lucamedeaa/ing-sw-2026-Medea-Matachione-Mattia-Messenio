package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.buildings.VictoryPoints;
import it.polimi.ingsw.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VictoryPointsTest {

    private VictoryPoints victoryPoints;
    private Player player;

    @BeforeEach
    void setUp() {
        victoryPoints = new VictoryPoints(0, 5, 1);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    @Test
    void testFinalPointsCorrectCalculation() {
        assertEquals(30, victoryPoints.getFinalPoints(player),
                "Deve restituire prestigePoints + 25");
    }

    @Test
    void testFinalPointsIndependentFromPlayer() {
        Player other = newPlayer("Bob");

        int result1 = victoryPoints.getFinalPoints(player);
        int result2 = victoryPoints.getFinalPoints(other);

        assertEquals(result1, result2,
                "Il risultato non deve dipendere dal Player");
    }

    @Test
    void testMultipleCallsConsistency() {
        int first = victoryPoints.getFinalPoints(player);
        int second = victoryPoints.getFinalPoints(player);

        assertEquals(first, second,
                "Chiamate multiple devono dare lo stesso risultato");
    }

    @Test
    void testDifferentPrestigeValues() {
        VictoryPoints vp = new VictoryPoints(0, 10, 1);

        assertEquals(35, vp.getFinalPoints(player),
                "Deve funzionare con diversi valori di prestigePoints");
    }
}