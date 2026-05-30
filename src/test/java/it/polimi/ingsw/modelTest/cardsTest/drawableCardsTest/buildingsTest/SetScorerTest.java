package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.buildingsTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.SetScorer;
import it.polimi.ingsw.server.model.card.building.VictoryPoints;
import it.polimi.ingsw.server.model.card.character.*;
import it.polimi.ingsw.server.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SetScorerTest {

    private SetScorer setScorer;
    private Player player;

    @BeforeEach
    void setUp() {
        setScorer = new SetScorer(107);
        player = newPlayer("Alice");
    }

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    /**
     * SUMMARY:
     * Verifies that SetScorer returns only its base prestige when no complete character set exists.
     *
     * EXPECTATION:
     * getFinalPoints returns 6 (base prestige only, no set bonus).
     */
    @Test
    void testNoCompleteSet() {
        player.addCard(new Hunter(10)); // false
        player.addCard(new Artist(19));
        player.addCard(new Builder(1));

        assertEquals(6, setScorer.getFinalPoints(player),
                "Senza set completi deve restituire solo i prestigePoints base");
    }

    /**
     * SUMMARY:
     * Verifies that SetScorer awards a 6-point bonus for one complete set of all 6 character types.
     *
     * EXPECTATION:
     * getFinalPoints returns 12 (6 base + 6 bonus for one complete set).
     */
    @Test
    void testOneCompleteSet() {
        player.addCard(new Hunter(10));
        player.addCard(new Artist(19));
        player.addCard(new Builder(1));
        player.addCard(new Collector(35));
        player.addCard(new Shaman(28));
        player.addCard(new Inventor(39));

        assertEquals(12, setScorer.getFinalPoints(player),
                "Un set completo deve dare 6 punti bonus + 6 base");
    }

    /**
     * SUMMARY:
     * Verifies that SetScorer awards 12 bonus points for two complete character sets.
     *
     * EXPECTATION:
     * getFinalPoints returns 18 (6 base + 12 bonus for two complete sets).
     */
    @Test
    void testTwoCompleteSets() {
        player.addCard(new Hunter(10));
        player.addCard(new Hunter(11));
        player.addCard(new Artist(19));
        player.addCard(new Artist(20));
        player.addCard(new Builder(1));
        player.addCard(new Builder(2));
        player.addCard(new Collector(35));
        player.addCard(new Collector(36));
        player.addCard(new Shaman(28));
        player.addCard(new Shaman(29));
        player.addCard(new Inventor(39));
        player.addCard(new Inventor(41));

        assertEquals(18, setScorer.getFinalPoints(player),
                "Due set completi devono dare 12 punti bonus + 6 base");
    }

    /**
     * SUMMARY:
     * Verifies that building cards are excluded from the character set count.
     *
     * EXPECTATION:
     * A VictoryPoints building does not substitute for the missing Inventor; result is base 6 only.
     */
    @Test
    void testBuildingsDoNotCountInSet() {
        player.addCard(new Hunter(10));
        player.addCard(new Artist(19));
        player.addCard(new Builder(1));
        player.addCard(new Collector(35));
        player.addCard(new Shaman(28));
        player.addCard(new VictoryPoints(109));

        assertEquals(6, setScorer.getFinalPoints(player),
                "Gli edifici non devono contribuire al completamento del set");
    }

    /**
     * SUMMARY:
     * Verifies that the number of completed sets is determined by the minimum count across all character types.
     *
     * EXPECTATION:
     * Extra hunters (3) don't create a third set; result is 18 (6 base + 12 for two complete sets).
     */
    @Test
    void testMinimumCountDeterminesCompletedSets() {
        player.addCard(new Hunter(10));
        player.addCard(new Hunter(11));
        player.addCard(new Hunter(12));
        player.addCard(new Artist(19));
        player.addCard(new Artist(20));
        player.addCard(new Builder(1));
        player.addCard(new Builder(2));
        player.addCard(new Collector(35));
        player.addCard(new Collector(36));
        player.addCard(new Shaman(28));
        player.addCard(new Shaman(29));
        player.addCard(new Inventor(39));
        player.addCard(new Inventor(41));

        assertEquals(18, setScorer.getFinalPoints(player),
                "Il numero di set completi deve essere determinato dal minimo tra i tipi presenti");
    }
}