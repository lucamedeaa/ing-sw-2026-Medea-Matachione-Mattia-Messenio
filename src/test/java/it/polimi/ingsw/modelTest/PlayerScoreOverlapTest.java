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

    @Test
    @DisplayName("Punti Prestigio base senza carte")
    void testBasicPrestigePointsOnly() {
        Player p = newPlayer("TestPlayer");
        p.addPrestige(15);
        assertEquals(15, p.calculateTotalScore());
    }

    @Test
    @DisplayName("Sovrapposizione: Costruttori ed Edifici")
    void testBuildersAndBuildingsScoring() {
        Player p = newPlayer("TestPlayer");
        give(p, new Builder(3)); // Fornisce 3 PP a fine partita
        give(p, new VictoryPoints(109)); // Fornisce 25 PP
        assertEquals(28, p.calculateTotalScore());
    }

    @Test
    @DisplayName("Sovrapposizione complessa: Prestigio + Artisti + Inventori + Edifici + Costruttori")
    void testComplexOverlapping() {
        Player p = newPlayer("TestPlayer");

        p.addPrestige(5);

        give(p, new Artist(19));
        give(p, new Artist(20));
        give(p, new Artist(21)); // 1 coppia -> +10

        give(p, new Inventor(39)); // SPEARHEAD
        give(p, new Inventor(44)); // ROPE
        give(p, new Inventor(39)); // Duplicato. Totale Inventori 3. Icone 2. -> 3*2 = +6

        give(p, new Builder(4)); // +4 PP

        give(p, new VictoryPoints(109)); // +25 PP

        // 5 + 10 + 6 + 4 + 25 = 50
        assertEquals(50, p.calculateTotalScore());
    }
}