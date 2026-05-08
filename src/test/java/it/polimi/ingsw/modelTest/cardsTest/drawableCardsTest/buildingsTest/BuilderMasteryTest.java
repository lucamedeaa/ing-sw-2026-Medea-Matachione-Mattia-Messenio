package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.BuilderMastery;
import it.polimi.ingsw.server.model.card.character.Builder;
import it.polimi.ingsw.server.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BuilderMasteryTest {

    private BuilderMastery builderMastery;
    private Player player;

    @BeforeEach
    void setUp() {
        // Updated Constructor: idcard, foodCost, prestigePoints, era
        builderMastery = new BuilderMastery(103, 6, 4, 2);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    @Test
    void testNoBuilders() {
        player.addCard(builderMastery);
        assertEquals(4, builderMastery.getFinalPoints(player),
                "Senza builder deve restituire solo i prestigePoints dell'edificio");
    }

    @Test
    void testSingleBuilder() {
        // Builder Constructor: idcard, era, foodDiscount, endGamePrestigePoints
        player.addCard(new Builder(1, 1, 1, 2));
        player.addCard(builderMastery);

        // Builder PP (2) + BuilderMastery PP (4) = 6
        assertEquals(6, builderMastery.getFinalPoints(player),
                "1 builder (2 PP) + prestige (4) = 6");
    }

    @Test
    void testMultipleBuilders() {
        player.addCard(new Builder(1, 1, 1, 2));
        player.addCard(new Builder(2, 1, 2, 0));
        player.addCard(builderMastery);

        // (2 + 0) + 4 = 6
        assertEquals(6, builderMastery.getFinalPoints(player),
                "Somma builder + prestigePoints");
    }

    @Test
    void testOnlyCountsBuilders() {
        player.addCard(new Builder(1, 1, 1, 2));
        player.addCard(new BuilderMastery(103, 6, 4, 2)); // Another building
        player.addCard(builderMastery);

        // Builder PP (2) + BuilderMastery PP (4) = 6
        assertEquals(6, builderMastery.getFinalPoints(player),
                "Deve contare solo i Builder");
    }

    @Test
    void testMultipleCallsConsistency() {
        player.addCard(new Builder(1, 1, 1, 2));
        player.addCard(builderMastery);

        int first = builderMastery.getFinalPoints(player);
        int second = builderMastery.getFinalPoints(player);

        assertEquals(first, second, "Chiamate multiple devono dare lo stesso risultato");
    }
}