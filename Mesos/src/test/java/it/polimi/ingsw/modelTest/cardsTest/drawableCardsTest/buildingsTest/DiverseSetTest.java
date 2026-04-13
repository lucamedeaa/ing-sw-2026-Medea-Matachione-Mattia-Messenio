package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import it.polimi.ingsw.model.cards.drawableCards.buildings.DiverseSet;
import it.polimi.ingsw.model.cards.drawableCards.buildings.VictoryPoints;
import it.polimi.ingsw.model.cards.drawableCards.characters.*;
import it.polimi.ingsw.model.enums.InventorIcon;
import it.polimi.ingsw.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DiverseSetTest {

    private DiverseSet diverseSet;
    private Player player;

    @BeforeEach
    void setUp() {
        diverseSet = new DiverseSet(0, 0, 1);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    private void give(Player p, DrawableCard card) {
        p.addCard(card);
    }

    @Test
    void testNoRewardOnAcquisition() {
        give(player, new Hunter(1, false));
        give(player, new Artist(1));
        give(player, new Builder(1, 0, 0));
        give(player, new Collector(1, 0));
        give(player, new Shaman(1, 1));
        give(player, new Inventor(1, InventorIcon.CANOE));

        give(player, diverseSet);

        assertEquals(0, player.getFood(),
                "I set già completi al momento dell'acquisto non devono dare cibo");
    }

    @Test
    void testOneNewCompleteSetGivesFiveFood() {
        give(player, diverseSet);

        give(player, new Hunter(1, false));
        give(player, new Artist(1));
        give(player, new Builder(1, 0, 0));
        give(player, new Collector(1, 0));
        give(player, new Shaman(1, 1));
        give(player, new Inventor(1, InventorIcon.CANOE));

        assertEquals(5, player.getFood(),
                "Il completamento di un set completo deve dare 5 cibo");
    }

    @Test
    void testTwoNewCompleteSetsGiveTenFood() {
        give(player, diverseSet);

        give(player, new Hunter(1, false));
        give(player, new Hunter(1, false));

        give(player, new Artist(1));
        give(player, new Artist(1));

        give(player, new Builder(1, 0, 0));
        give(player, new Builder(1, 0, 0));

        give(player, new Collector(1, 0));
        give(player, new Collector(1, 0));

        give(player, new Shaman(1, 1));
        give(player, new Shaman(1, 1));

        give(player, new Inventor(1, InventorIcon.CANOE));
        give(player, new Inventor(1, InventorIcon.BREAD));

        assertEquals(10, player.getFood(),
                "Il completamento di due set completi deve dare 10 cibo");
    }

    @Test
    void testIncompleteSetGivesNoFood() {
        give(player, diverseSet);

        give(player, new Hunter(1, false));
        give(player, new Artist(1));
        give(player, new Builder(1, 0, 0));
        give(player, new Collector(1, 0));
        give(player, new Shaman(1, 1));
        // manca Inventor

        assertEquals(0, player.getFood(),
                "Un set incompleto non deve dare cibo");
    }

    @Test
    void testBuildingDoesNotContributeToSet() {
        give(player, diverseSet);

        give(player, new Hunter(1, false));
        give(player, new Artist(1));
        give(player, new Builder(1, 0, 0));
        give(player, new Collector(1, 0));
        give(player, new Shaman(1, 1));
        give(player, new VictoryPoints(0, 0, 1));

        assertEquals(0, player.getFood(),
                "Gli edifici non devono contribuire al completamento del set");
    }

    @Test
    void testNoExtraRewardWithoutNewFullSet() {
        give(player, diverseSet);

        give(player, new Hunter(1, false));
        give(player, new Artist(1));
        give(player, new Builder(1, 0, 0));
        give(player, new Collector(1, 0));
        give(player, new Shaman(1, 1));
        give(player, new Inventor(1, InventorIcon.CANOE));

        assertEquals(5, player.getFood());

        give(player, new Hunter(1, false));

        assertEquals(5, player.getFood(),
                "Aggiungere carte senza completare un nuovo set non deve dare altro cibo");
    }
}