package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.VictoryPoints;
import it.polimi.ingsw.server.model.card.character.Artist;
import it.polimi.ingsw.server.model.card.character.Builder;
import it.polimi.ingsw.server.model.card.character.Inventor;
import it.polimi.ingsw.server.model.enums.InventorIcon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerScoreOverlapTest extends ModelTest {

    @Test
    @DisplayName("Punti Prestigio base senza carte")
    void testBasicPrestigePointsOnly() {
        Player p = newPlayer("TestPlayer");
        p.addPrestige(15);
        assertEquals(15, p.calculateTotalScore(), "Il punteggio deve riflettere solo i punti prestigio base");
    }

    @Test
    @DisplayName("Sovrapposizione: Costruttori ed Edifici")
    void testBuildersAndBuildingsScoring() {
        Player p = newPlayer("TestPlayer");

        // Builder(id, era, discount, finalPoints)
        give(p, new Builder(1, 1, 2, 3)); // Fornisce 3 PP a fine partita

        // VictoryPoints(id, era, prestigePoints, unknown/cost) -> 5 PP base + 25 fissi = 30 PP (come dal tuo PlayerTest)
        give(p, new VictoryPoints(2, 0, 5, 1));

        assertEquals(33, p.calculateTotalScore(), "Deve sommare i punti del Costruttore e dell'Edificio");
    }

    @Test
    @DisplayName("Sovrapposizione complessa: Prestigio + Artisti + Inventori + Edifici + Costruttori")
    void testComplexOverlapping() {
        Player p = newPlayer("TestPlayer");

        // 1. Prestigio base: +5 PP
        p.addPrestige(5);

        // 2. Artisti: 3 Artisti danno 10 PP (divisione intera 3/2 = 1 coppia -> 1 * 10)
        give(p, new Artist(10, 1));
        give(p, new Artist(11, 1));
        give(p, new Artist(12, 1));

        // 3. Inventori: 2 Inventori con 2 icone distinte (2 * 2 = 4 PP)
        give(p, new Inventor(20, 1, InventorIcon.CANOE));
        give(p, new Inventor(21, 1, InventorIcon.ROPE));

        // 4. Inventori duplicati: 1 Inventore extra ma con icona già presente (Totale Inventori: 3. Icone: 2 -> 3 * 2 = 6 PP)
        give(p, new Inventor(22, 1, InventorIcon.CANOE));

        // 5. Costruttori: 1 Costruttore che dà +4 PP a fine partita
        give(p, new Builder(30, 1, 2, 4));

        // 6. Edificio speciale: +30 PP (5 + 25)
        give(p, new VictoryPoints(40, 0, 5, 1));

        // Calcolo atteso:
        // Prestigio base = 5
        // Artisti = 10
        // Inventori = 6 (3 inventori * 2 icone)
        // Costruttori = 4
        // Edifici = 30
        // Totale = 5 + 10 + 6 + 4 + 30 = 55
        assertEquals(55, p.calculateTotalScore(), "Il calcolo sovrapposto fallisce nel sommare tutte le fonti di punteggio");
    }
}