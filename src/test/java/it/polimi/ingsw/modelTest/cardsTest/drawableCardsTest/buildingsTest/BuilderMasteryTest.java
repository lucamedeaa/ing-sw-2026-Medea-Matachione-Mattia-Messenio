package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import it.polimi.ingsw.model.cards.drawableCards.buildings.BuilderMastery;
import it.polimi.ingsw.model.cards.drawableCards.characters.Builder;
import it.polimi.ingsw.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BuilderMasteryTest {

    private BuilderMastery builderMastery;
    private Player player;

    @BeforeEach
    void setUp() {
        builderMastery = new BuilderMastery(0, 4, 1);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    private void give(Player p, DrawableCard card) {
        p.addCard(card);
    }

    @Test
    void testNoBuilders() {
        give(player, builderMastery);

        assertEquals(4, builderMastery.getFinalPoints(player),
                "Senza builder deve restituire solo i prestigePoints dell'edificio");
    }

    @Test
    void testSingleBuilder() {
        give(player, new Builder(1, 0, 3)); // builder da 3 punti
        give(player, builderMastery);

        assertEquals(7, builderMastery.getFinalPoints(player),
                "1 builder (3) + prestige (4) = 7");
    }

    @Test
    void testMultipleBuilders() {
        give(player, new Builder(1, 0, 3));
        give(player, new Builder(1, 0, 2));
        give(player, builderMastery);

        // (3 + 2) + 4 = 9
        assertEquals(9, builderMastery.getFinalPoints(player),
                "Somma builder + prestigePoints");
    }

    @Test
    void testOnlyCountsBuilders() {
        give(player, new Builder(1, 0, 3));
        give(player, new BuilderMastery(0, 4, 1)); // altro edificio, non builder
        give(player, builderMastery);

        assertEquals(7, builderMastery.getFinalPoints(player),
                "Deve contare solo i Builder");
    }

    @Test
    void testMultipleCallsConsistency() {
        give(player, new Builder(1, 0, 3));
        give(player, builderMastery);

        int first = builderMastery.getFinalPoints(player);
        int second = builderMastery.getFinalPoints(player);

        assertEquals(first, second, "Chiamate multiple devono dare lo stesso risultato");
    }
}