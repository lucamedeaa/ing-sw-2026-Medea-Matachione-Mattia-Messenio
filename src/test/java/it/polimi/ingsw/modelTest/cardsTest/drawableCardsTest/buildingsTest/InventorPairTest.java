package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.InventorPair;
import it.polimi.ingsw.server.model.card.character.Inventor;
import it.polimi.ingsw.server.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InventorPairTest {

    private InventorPair inventorPair;
    private Player player;

    @BeforeEach
    void setUp() {
        inventorPair = new InventorPair(98);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    @Test
    void testNoRewardOnAcquisitionWithPreExistingPair() {
        player.addCard(new Inventor(39)); // SPEARHEAD
        player.addCard(new Inventor(72)); // SPEARHEAD
        player.addCard(inventorPair);
        assertEquals(0, player.getFood());
    }

    @Test
    void testSingleInventorAfterAcquisitionGivesNoFood() {
        player.addCard(inventorPair);
        player.addCard(new Inventor(39));
        assertEquals(0, player.getFood());
    }

    @Test
    void testNewPairAfterAcquisitionGivesThreeFood() {
        player.addCard(inventorPair);
        player.addCard(new Inventor(39)); // SPEARHEAD
        player.addCard(new Inventor(72)); // SPEARHEAD
        assertEquals(3, player.getFood());
    }

    @Test
    void testDifferentIconsDoNotFormPair() {
        player.addCard(inventorPair);
        player.addCard(new Inventor(39)); // SPEARHEAD
        player.addCard(new Inventor(41)); // BREAD
        assertEquals(0, player.getFood());
    }

    @Test
    void testTwoNewPairsGiveSixFood() {
        player.addCard(inventorPair);
        player.addCard(new Inventor(39));
        player.addCard(new Inventor(72)); // +3
        player.addCard(new Inventor(41)); // BREAD
        player.addCard(new Inventor(52)); // BREAD (+3)
        assertEquals(6, player.getFood());
    }

    @Test
    void testPreExistingSingleInventorAndOneNewMatchingInventorGiveThreeFood() {
        player.addCard(new Inventor(39));
        player.addCard(inventorPair);
        player.addCard(new Inventor(72));
        assertEquals(3, player.getFood());
    }

    @Test
    void testThirdInventorOfSameIconDoesNotGiveMoreFood() {
        player.addCard(inventorPair);
        player.addCard(new Inventor(39));
        player.addCard(new Inventor(72)); // +3
        player.addCard(new Inventor(72)); // No new pair
        assertEquals(3, player.getFood());
    }
}