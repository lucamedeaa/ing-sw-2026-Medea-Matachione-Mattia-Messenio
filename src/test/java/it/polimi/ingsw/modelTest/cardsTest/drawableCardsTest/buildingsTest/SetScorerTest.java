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
        setScorer = new SetScorer(0, 2, 1);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    private void give(Player p, DrawableCard card) {
        p.addCard(card);
    }

    @Test
    void testNoCompleteSet() {
        give(player, new Hunter(1, false));
        give(player, new Artist(1));
        give(player, new Builder(1, 0, 0));

        assertEquals(2, setScorer.getFinalPoints(player),
                "Senza set completi deve restituire solo i prestigePoints base");
    }

    @Test
    void testOneCompleteSet() {
        give(player, new Hunter(1, false));
        give(player, new Artist(1));
        give(player, new Builder(1, 0, 0));
        give(player, new Collector(1, 0));
        give(player, new Shaman(1, 1));
        give(player, new Inventor(1, InventorIcon.CANOE));

        assertEquals(8, setScorer.getFinalPoints(player),
                "Un set completo deve dare 6 punti bonus + 2 base");
    }

    @Test
    void testTwoCompleteSets() {
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

        assertEquals(14, setScorer.getFinalPoints(player),
                "Due set completi devono dare 12 punti bonus + 2 base");
    }

    @Test
    void testBuildingsDoNotCountInSet() {
        give(player, new Hunter(1, false));
        give(player, new Artist(1));
        give(player, new Builder(1, 0, 0));
        give(player, new Collector(1, 0));
        give(player, new Shaman(1, 1));
        give(player, new VictoryPoints(0, 0, 1));

        assertEquals(2, setScorer.getFinalPoints(player),
                "Gli edifici non devono contribuire al completamento del set");
    }

    @Test
    void testMinimumCountDeterminesCompletedSets() {
        give(player, new Hunter(1, false));
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

        assertEquals(14, setScorer.getFinalPoints(player),
                "Il numero di set completi deve essere determinato dal minimo tra i tipi presenti");
    }
}