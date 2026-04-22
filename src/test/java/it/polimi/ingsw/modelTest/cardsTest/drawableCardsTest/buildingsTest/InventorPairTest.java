package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.buildings.InventorPair;
import it.polimi.ingsw.model.cards.drawableCards.characters.Inventor;
import it.polimi.ingsw.model.enums.InventorIcon;
import it.polimi.ingsw.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InventorPairTest {

    private InventorPair inventorPair;
    private Player player;

    @BeforeEach
    void setUp() {
        inventorPair = new InventorPair(0, 0, 1);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    private void give(Player p, DrawableCard card) {
        p.addCard(card);
    }

    @Test
    void testNoRewardOnAcquisitionWithPreExistingPair() {
        give(player, new Inventor(1, InventorIcon.CANOE));
        give(player, new Inventor(1, InventorIcon.CANOE));

        give(player, inventorPair);

        assertEquals(0, player.getFood(),
                "Le coppie già presenti al momento dell'acquisto non devono dare cibo");
    }

    @Test
    void testSingleInventorAfterAcquisitionGivesNoFood() {
        give(player, inventorPair);
        give(player, new Inventor(1, InventorIcon.CANOE));

        assertEquals(0, player.getFood(),
                "Un solo inventore non deve dare cibo");
    }

    @Test
    void testNewPairAfterAcquisitionGivesThreeFood() {
        give(player, inventorPair);
        give(player, new Inventor(1, InventorIcon.CANOE));
        give(player, new Inventor(1, InventorIcon.CANOE));

        assertEquals(3, player.getFood(),
                "Una nuova coppia post-acquisto deve dare 3 cibo");
    }

    @Test
    void testDifferentIconsDoNotFormPair() {
        give(player, inventorPair);
        give(player, new Inventor(1, InventorIcon.CANOE));
        give(player, new Inventor(1, InventorIcon.BREAD));

        assertEquals(0, player.getFood(),
                "Inventori con icone diverse non devono formare una coppia");
    }

    @Test
    void testTwoNewPairsGiveSixFood() {
        give(player, inventorPair);

        give(player, new Inventor(1, InventorIcon.CANOE));
        give(player, new Inventor(1, InventorIcon.CANOE)); // +3

        give(player, new Inventor(1, InventorIcon.BREAD));
        give(player, new Inventor(1, InventorIcon.BREAD)); // +3

        assertEquals(6, player.getFood(),
                "Due coppie nuove devono dare 6 cibo");
    }

    @Test
    void testPreExistingSingleInventorAndOneNewMatchingInventorGiveThreeFood() {
        give(player, new Inventor(1, InventorIcon.CANOE));
        give(player, inventorPair);
        give(player, new Inventor(1, InventorIcon.CANOE));

        assertEquals(3, player.getFood(),
                "Un inventore già presente e uno nuovo uguale devono completare una coppia e dare 3 cibo");
    }

    @Test
    void testThirdInventorOfSameIconDoesNotGiveMoreFood() {
        give(player, inventorPair);
        give(player, new Inventor(1, InventorIcon.CANOE));
        give(player, new Inventor(1, InventorIcon.CANOE)); // +3
        give(player, new Inventor(1, InventorIcon.CANOE)); // nessuna nuova coppia

        assertEquals(3, player.getFood(),
                "Il terzo inventore uguale non deve dare altro cibo");
    }
}