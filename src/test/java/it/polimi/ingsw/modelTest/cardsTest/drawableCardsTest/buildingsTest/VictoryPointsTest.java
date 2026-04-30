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
        // Updated Constructor: idcard, foodCost, prestigePoints, era
        victoryPoints = new VictoryPoints(109, 10, 0, 3);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    @Test
    void testFinalPointsCorrectCalculation() {
        assertEquals(25, victoryPoints.getFinalPoints(player),
                "Deve restituire prestigePoints (0) + 25");
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
        VictoryPoints vp = new VictoryPoints(109, 10, 10, 3);
        assertEquals(35, vp.getFinalPoints(player),
                "Deve funzionare con diversi valori di prestigePoints");
    }
}