package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.VictoryPoints;
import it.polimi.ingsw.server.model.card.character.Artist;
import it.polimi.ingsw.server.model.card.character.Builder;
import it.polimi.ingsw.server.model.card.character.Inventor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerScoreOverlapTest extends ModelTest {

    /**
     * SUMMARY:
     * Verifies that a player with only prestige points and no cards
     * has a total score equal to their prestige.
     *
     * EXPECTATION:
     * calculateTotalScore() returns 15 when the player has 15 prestige and no cards.
     */
    @Test
    @DisplayName("calculateTotalScore with only prestige points")
    void totalScoreOnlyPrestige() {
        Player p = newPlayer("Alice");
        p.addPrestige(15);
        assertEquals(15, p.calculateTotalScore());
    }

    /**
     * SUMMARY:
     * Verifies that a single artist does not contribute any bonus points,
     * since artist scoring requires pairs.
     *
     * EXPECTATION:
     * calculateTotalScore() returns 0 with only one artist and no prestige.
     */
    @Test
    @DisplayName("1 artist contributes no bonus (bonus requires pairs)")
    void oneArtistNoBonus() {
        Player p = newPlayer("Alice");
        give(p, new Artist(19));
        assertEquals(0, p.calculateTotalScore());
    }

    /**
     * SUMMARY:
     * Verifies that 4 artists produce 20 bonus points according to
     * the pair-based artist scoring formula.
     *
     * EXPECTATION:
     * calculateTotalScore() returns 20 with 4 artists.
     */
    @Test
    @DisplayName("4 artists contribute 20 points")
    void fourArtistsGiveTwentyPoints() {
        Player p = newPlayer("Alice");
        give(p, new Artist(19));
        give(p, new Artist(20));
        give(p, new Artist(21));
        give(p, new Artist(22));
        assertEquals(20, p.calculateTotalScore());
    }

    /**
     * SUMMARY:
     * Verifies that a VictoryPoints building card contributes its
     * prestige points to the end-game total score.
     *
     * EXPECTATION:
     * calculateTotalScore() returns 25 with a single VictoryPoints(109) building.
     */
    @Test
    @DisplayName("VictoryPoints building adds prestigePoints at end game")
    void victoryPointsBuildingScore() {
        Player p = newPlayer("Alice");
        give(p, new VictoryPoints(109));
        assertEquals(25, p.calculateTotalScore());
    }

    /**
     * SUMMARY:
     * Verifies that a Builder card contributes its end-game prestige
     * bonus to the total score.
     *
     * EXPECTATION:
     * calculateTotalScore() returns 2 with a single Builder(1).
     */
    @Test
    @DisplayName("Builder contributes endGamePrestigePoints to total score")
    void builderFinalPoints() {
        Player p = newPlayer("Alice");
        give(p, new Builder(1));
        assertEquals(2, p.calculateTotalScore());
    }


    /**
     * SUMMARY:
     * Verifies that Builder end-game prestige and VictoryPoints building
     * prestige are correctly summed when both card types are present.
     *
     * EXPECTATION:
     * calculateTotalScore() returns 28 (3 from Builder + 25 from VictoryPoints).
     */
    @Test
    @DisplayName("Overlap: Builders and Buildings")
    void testBuildersAndBuildingsScoring() {
        Player p = newPlayer("TestPlayer");
        give(p, new Builder(3)); // Provides 3 PP at the end of the game
        give(p, new VictoryPoints(109)); // Provides 25 PP
        assertEquals(28, p.calculateTotalScore());
    }

    /**
     * SUMMARY:
     * Verifies a complex scoring scenario combining prestige points,
     * artist pair bonuses, inventor icon scoring, builder end-game
     * prestige, and a VictoryPoints building — all at once.
     *
     * EXPECTATION:
     * calculateTotalScore() returns 50 (5 prestige + 10 artists + 6 inventors
     * + 4 builder + 25 building).
     */
    @Test
    @DisplayName("Complex Overlap: Prestige + Artists + Inventors + Buildings + Builders")
    void testComplexOverlapping() {
        Player p = newPlayer("TestPlayer");

        p.addPrestige(5);

        give(p, new Artist(19));
        give(p, new Artist(20));
        give(p, new Artist(21)); // 1 pair -> +10

        give(p, new Inventor(39)); // SPEARHEAD
        give(p, new Inventor(44)); // ROPE
        give(p, new Inventor(39)); // Duplicate. Total Inventors 3. Distinct Icons 2. -> 3*2 = +6

        give(p, new Builder(4)); // +4 PP

        give(p, new VictoryPoints(109)); // +25 PP

        // 5 + 10 + 6 + 4 + 25 = 50
        assertEquals(50, p.calculateTotalScore());
    }

    /**
     * SUMMARY:
     * Verifies the inventor scoring formula when all inventors share
     * the same icon, producing only 1 distinct icon.
     *
     * EXPECTATION:
     * 3 inventors × 1 distinct icon = 3 points total.
     */
    @Test
    @DisplayName("3 inventors with identical icons score 3 (n × 1 distinct)")
    void threeInventorsWithSameIconScoreThree() {
        Player p = newPlayer("X");
        p.addCard(new Inventor(39)); // SPEARHEAD
        p.addCard(new Inventor(72)); // SPEARHEAD
        p.addCard(new Inventor(72)); // SPEARHEAD

        assertEquals(3, p.calculateTotalScore());
    }
}