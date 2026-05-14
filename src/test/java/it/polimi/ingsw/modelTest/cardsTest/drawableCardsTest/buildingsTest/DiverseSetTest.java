package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.DiverseSet;
import it.polimi.ingsw.server.model.card.building.VictoryPoints;
import it.polimi.ingsw.server.model.card.character.*;
import it.polimi.ingsw.server.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DiverseSetTest {

    private DiverseSet diverseSet;
    private Player player;

    @BeforeEach
    void setUp() {
        diverseSet = new DiverseSet(97);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    @Test
    void testNoRewardOnAcquisition() {
        player.addCard(new Hunter(12));
        player.addCard(new Artist(19));
        player.addCard(new Builder(1));
        player.addCard(new Collector(35));
        player.addCard(new Shaman(28));
        player.addCard(new Inventor(39));
        player.addCard(diverseSet);
        assertEquals(0, player.getFood());
    }

    @Test
    void testOneNewCompleteSetGivesFiveFood() {
        player.addCard(diverseSet);
        player.addCard(new Hunter(12));
        player.addCard(new Artist(19));
        player.addCard(new Builder(1));
        player.addCard(new Collector(35));
        player.addCard(new Shaman(28));
        player.addCard(new Inventor(39));
        assertEquals(5, player.getFood());
    }

    @Test
    void testTwoNewCompleteSetsGiveTenFood() {
        player.addCard(diverseSet);
        player.addCard(new Hunter(12));
        player.addCard(new Hunter(14));
        player.addCard(new Artist(19));
        player.addCard(new Artist(20));
        player.addCard(new Builder(1));
        player.addCard(new Builder(2));
        player.addCard(new Collector(35));
        player.addCard(new Collector(36));
        player.addCard(new Shaman(28));
        player.addCard(new Shaman(29));
        player.addCard(new Inventor(39));
        player.addCard(new Inventor(40));
        assertEquals(10, player.getFood());
    }

    @Test
    void testIncompleteSetGivesNoFood() {
        player.addCard(diverseSet);
        player.addCard(new Hunter(12));
        player.addCard(new Artist(19));
        player.addCard(new Builder(1));
        player.addCard(new Collector(35));
        player.addCard(new Shaman(28));
        assertEquals(0, player.getFood());
    }

    @Test
    void testBuildingDoesNotContributeToSet() {
        player.addCard(diverseSet);
        player.addCard(new Hunter(12));
        player.addCard(new Artist(19));
        player.addCard(new Builder(1));
        player.addCard(new Collector(35));
        player.addCard(new Shaman(28));
        player.addCard(new VictoryPoints(109));
        assertEquals(0, player.getFood());
    }

    @Test
    void testNoExtraRewardWithoutNewFullSet() {
        player.addCard(diverseSet);
        player.addCard(new Hunter(12));
        player.addCard(new Artist(19));
        player.addCard(new Builder(1));
        player.addCard(new Collector(35));
        player.addCard(new Shaman(28));
        player.addCard(new Inventor(39));
        assertEquals(5, player.getFood());

        player.addCard(new Hunter(14));
        assertEquals(5, player.getFood());
    }
}