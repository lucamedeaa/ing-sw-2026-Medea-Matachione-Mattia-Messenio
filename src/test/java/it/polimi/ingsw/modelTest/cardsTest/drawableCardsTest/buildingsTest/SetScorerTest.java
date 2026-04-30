package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.buildings.SetScorer;
import it.polimi.ingsw.model.cards.drawableCards.buildings.VictoryPoints;
import it.polimi.ingsw.model.cards.drawableCards.characters.*;
import it.polimi.ingsw.model.enums.InventorIcon;
import it.polimi.ingsw.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SetScorerTest {

    private SetScorer setScorer;
    private Player player;

    @BeforeEach
    void setUp() {
        setScorer = new SetScorer(107, 5, 6, 2);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    @Test
    void testNoCompleteSet() {
        player.addCard(new Hunter(10, 1, false)); // false
        player.addCard(new Artist(19, 1));
        player.addCard(new Builder(1, 1, 1, 2));

        assertEquals(6, setScorer.getFinalPoints(player),
                "Senza set completi deve restituire solo i prestigePoints base");
    }

    @Test
    void testOneCompleteSet() {
        player.addCard(new Hunter(10, 1, false));
        player.addCard(new Artist(19, 1));
        player.addCard(new Builder(1, 1, 1, 2));
        player.addCard(new Collector(35, 1, 3));
        player.addCard(new Shaman(28, 1, 1));
        player.addCard(new Inventor(39, 1, InventorIcon.CANOE));

        assertEquals(12, setScorer.getFinalPoints(player),
                "Un set completo deve dare 6 punti bonus + 6 base");
    }

    @Test
    void testTwoCompleteSets() {
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
        player.addCard(new Inventor(39, 1, InventorIcon.CANOE));
        player.addCard(new Inventor(41, 1, InventorIcon.BREAD));

        assertEquals(18, setScorer.getFinalPoints(player),
                "Due set completi devono dare 12 punti bonus + 6 base");
    }

    @Test
    void testBuildingsDoNotCountInSet() {
        player.addCard(new Hunter(10, 1, false));
        player.addCard(new Artist(19, 1));
        player.addCard(new Builder(1, 1, 1, 2));
        player.addCard(new Collector(35, 1, 3));
        player.addCard(new Shaman(28, 1, 1));
        player.addCard(new VictoryPoints(109, 10, 0, 3));

        assertEquals(6, setScorer.getFinalPoints(player),
                "Gli edifici non devono contribuire al completamento del set");
    }

    @Test
    void testMinimumCountDeterminesCompletedSets() {
        player.addCard(new Hunter(10, 1, false));
        player.addCard(new Hunter(11, 1, false));
        player.addCard(new Hunter(12, 1, false));
        player.addCard(new Artist(19, 1));
        player.addCard(new Artist(20, 1));
        player.addCard(new Builder(1, 1, 1, 2));
        player.addCard(new Builder(2, 1, 2, 0));
        player.addCard(new Collector(35, 1, 3));
        player.addCard(new Collector(36, 1, 3));
        player.addCard(new Shaman(28, 1, 1));
        player.addCard(new Shaman(29, 1, 2));
        player.addCard(new Inventor(39, 1, InventorIcon.CANOE));
        player.addCard(new Inventor(41, 1, InventorIcon.BREAD));

        assertEquals(18, setScorer.getFinalPoints(player),
                "Il numero di set completi deve essere determinato dal minimo tra i tipi presenti");
    }
}