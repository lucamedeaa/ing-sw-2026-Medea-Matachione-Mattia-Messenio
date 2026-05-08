package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.InventorPair;
import it.polimi.ingsw.server.model.card.character.Inventor;
import it.polimi.ingsw.server.model.enums.InventorIcon;
import it.polimi.ingsw.server.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InventorPairTest {

    private InventorPair inventorPair;
    private Player player;

    @BeforeEach
    void setUp() {
        // Updated Constructor: idcard, foodCost, prestigePoints, era
        inventorPair = new InventorPair(98, 3, 4, 1);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    @Test
    void testNoRewardOnAcquisitionWithPreExistingPair() {
        player.addCard(new Inventor(39, 1, InventorIcon.CANOE));
        player.addCard(new Inventor(42, 1, InventorIcon.CANOE));

        player.addCard(inventorPair);

        assertEquals(0, player.getFood(),
                "Le coppie già presenti al momento dell'acquisto non devono dare cibo");
    }

    @Test
    void testSingleInventorAfterAcquisitionGivesNoFood() {
        player.addCard(inventorPair);

        player.addCard(new Inventor(39, 1, InventorIcon.CANOE));

        assertEquals(0, player.getFood(),
                "Un solo inventore non deve dare cibo");
    }

    @Test
    void testNewPairAfterAcquisitionGivesThreeFood() {
        player.addCard(inventorPair);

        player.addCard(new Inventor(39, 1, InventorIcon.CANOE));
        player.addCard(new Inventor(42, 1, InventorIcon.CANOE));

        assertEquals(3, player.getFood(),
                "Una nuova coppia post-acquisto deve dare 3 cibo");
    }

    @Test
    void testDifferentIconsDoNotFormPair() {
        player.addCard(inventorPair);

        player.addCard(new Inventor(39, 1, InventorIcon.CANOE));
        player.addCard(new Inventor(41, 1, InventorIcon.BREAD));

        assertEquals(0, player.getFood(),
                "Inventori con icone diverse non devono formare una coppia");
    }

    @Test
    void testTwoNewPairsGiveSixFood() {
        player.addCard(inventorPair);

        player.addCard(new Inventor(39, 1, InventorIcon.CANOE));
        player.addCard(new Inventor(42, 1, InventorIcon.CANOE)); // +3
        player.addCard(new Inventor(41, 1, InventorIcon.BREAD));
        player.addCard(new Inventor(52, 2, InventorIcon.BREAD)); // +3

        assertEquals(6, player.getFood(),
                "Due coppie nuove devono dare 6 cibo");
    }

    @Test
    void testPreExistingSingleInventorAndOneNewMatchingInventorGiveThreeFood() {
        player.addCard(new Inventor(39, 1, InventorIcon.CANOE));

        player.addCard(inventorPair);

        player.addCard(new Inventor(42, 1, InventorIcon.CANOE));

        assertEquals(3, player.getFood(),
                "Un inventore già presente e uno nuovo uguale devono completare una coppia e dare 3 cibo");
    }

    @Test
    void testThirdInventorOfSameIconDoesNotGiveMoreFood() {
        player.addCard(inventorPair);

        player.addCard(new Inventor(39, 1, InventorIcon.CANOE));
        player.addCard(new Inventor(42, 1, InventorIcon.CANOE)); // +3
        player.addCard(new Inventor(42, 1, InventorIcon.CANOE)); // No new pair

        assertEquals(3, player.getFood(),
                "Il terzo inventore uguale non deve dare altro cibo");
    }
}