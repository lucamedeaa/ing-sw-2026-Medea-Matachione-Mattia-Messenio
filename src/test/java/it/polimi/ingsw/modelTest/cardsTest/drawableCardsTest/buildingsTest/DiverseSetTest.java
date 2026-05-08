package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.DiverseSet;
import it.polimi.ingsw.server.model.card.building.VictoryPoints;
import it.polimi.ingsw.server.model.card.character.*;
import it.polimi.ingsw.server.model.enums.InventorIcon;
import it.polimi.ingsw.server.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DiverseSetTest {

    private DiverseSet diverseSet;
    private Player player;

    @BeforeEach
    void setUp() {
        diverseSet = new DiverseSet(97, 4, 3, 1);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    @Test
    void testNoRewardOnAcquisition() {
        player.addCard(new Hunter(10, 1, false)); // FALSE per sicurezza
        player.addCard(new Artist(19, 1));
        player.addCard(new Builder(1, 1, 1, 2));
        player.addCard(new Collector(35, 1, 3));
        player.addCard(new Shaman(28, 1, 1));
        player.addCard(new Inventor(39, 1, InventorIcon.SPEARHEAD));

        player.addCard(diverseSet);

        assertEquals(0, player.getFood(),
                "I set già completi al momento dell'acquisto non devono dare cibo");
    }

    @Test
    void testOneNewCompleteSetGivesFiveFood() {
        player.addCard(diverseSet);

        player.addCard(new Hunter(10, 1, false));
        player.addCard(new Artist(19, 1));
        player.addCard(new Builder(1, 1, 1, 2));
        player.addCard(new Collector(35, 1, 3));
        player.addCard(new Shaman(28, 1, 1));
        player.addCard(new Inventor(39, 1, InventorIcon.SPEARHEAD));

        assertEquals(5, player.getFood(),
                "Il completamento di un set completo deve dare 5 cibo");
    }

    @Test
    void testTwoNewCompleteSetsGiveTenFood() {
        player.addCard(diverseSet);

        player.addCard(new Hunter(10, 1, false));
        player.addCard(new Hunter(11, 1, false));
        player.addCard(new Artist(19, 1));
        player.addCard(new Artist(20, 1));
        player.addCard(new Builder(1, 1, 1, 2));
        player.addCard(new Builder(2, 1, 2, 0));
        player.addCard(new Collector(35, 1, 3));
        player.addCard(new Collector(36, 1, 3));
        player.addCard(new Shaman(28, 1, 1));
        player.addCard(new Shaman(29, 1, 2));
        player.addCard(new Inventor(39, 1, InventorIcon.SPEARHEAD));
        player.addCard(new Inventor(40, 1, InventorIcon.LEATHER));

        assertEquals(10, player.getFood(),
                "Il completamento di due set completi deve dare 10 cibo");
    }

    @Test
    void testIncompleteSetGivesNoFood() {
        player.addCard(diverseSet);

        player.addCard(new Hunter(10, 1, false));
        player.addCard(new Artist(19, 1));
        player.addCard(new Builder(1, 1, 1, 2));
        player.addCard(new Collector(35, 1, 3));
        player.addCard(new Shaman(28, 1, 1));

        assertEquals(0, player.getFood(),
                "Un set incompleto non deve dare cibo");
    }

    @Test
    void testBuildingDoesNotContributeToSet() {
        player.addCard(diverseSet);

        player.addCard(new Hunter(10, 1, false));
        player.addCard(new Artist(19, 1));
        player.addCard(new Builder(1, 1, 1, 2));
        player.addCard(new Collector(35, 1, 3));
        player.addCard(new Shaman(28, 1, 1));
        player.addCard(new VictoryPoints(109, 10, 0, 3));

        assertEquals(0, player.getFood(),
                "Gli edifici non devono contribuire al completamento del set");
    }

    @Test
    void testNoExtraRewardWithoutNewFullSet() {
        player.addCard(diverseSet);

        player.addCard(new Hunter(10, 1, false));
        player.addCard(new Artist(19, 1));
        player.addCard(new Builder(1, 1, 1, 2));
        player.addCard(new Collector(35, 1, 3));
        player.addCard(new Shaman(28, 1, 1));
        player.addCard(new Inventor(39, 1, InventorIcon.SPEARHEAD));

        assertEquals(5, player.getFood());

        player.addCard(new Hunter(11, 1, false));

        assertEquals(5, player.getFood(),
                "Aggiungere carte senza completare un nuovo set non deve dare altro cibo");
    }
}