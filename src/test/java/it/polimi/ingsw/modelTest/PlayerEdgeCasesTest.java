package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.character.Builder;
import it.polimi.ingsw.server.model.card.character.Collector;
import it.polimi.ingsw.server.model.card.character.Inventor;
import it.polimi.ingsw.server.model.enums.InventorIcon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerEdgeCasesTest extends ModelTest {

    @Test
    @DisplayName("Gestione Deficit: Transizione da cibo positivo a negativo")
    void testAddFoodPartialDeficit() {
        Player p = newPlayer("Test");
        p.addPrestige(10);
        p.addFood(2); // Il giocatore ha 2 cibo

        // Immagina di dover pagare 5 per l'ultimo spazio del totem o un evento base.
        // Il deficit è 3. Secondo l'hardcoding attuale in Player: -3 * 2 = -6 PP.
        p.addFood(-5);

        assertEquals(0, p.getFood(), "Il cibo non deve mai scendere sotto zero");
        assertEquals(4, p.getPrestigePoints(), "Da 10 PP deve scendere a 4 PP (penalità di -6)");

        // ATTENZIONE: Questo test passa con il tuo codice attuale, ma dimostra che
        // non hai modo di applicare una penalità di -3 PP se l'Evento Sostentamento di Era III lo richiede.
    }

    @Test
    @DisplayName("Rischio OOP: Doppio conteggio dello sconto Sostentamento")
    void testCollectorSustenanceDiscountLogic() {
        Player p = newPlayer("Test");

        // Aggiungiamo un Raccoglitore. Non sappiamo se la tua classe Collector
        // fa override di onSustenanceEvent, ma se lo fa, questo test fallirà indicando il doppio conteggio.
        give(p, new Collector(1, 1, 3));

        // Lo sconto deve essere esattamente 3 per un singolo Raccoglitore.
        assertEquals(3, p.getSustenanceDiscount(), "Se restituisce 6, c'è un doppio conteggio tra countCharacters e onSustenanceEvent");
    }

    @Test
    @DisplayName("Consistenza Architetturale: Builder contro Collector per FoodDiscount")
    void testCorrectDiscountDelegation() {
        Player p = newPlayer("Test");

        // Da regolamento: il Builder sconta l'acquisto di Edifici.
        give(p, new Builder(1, 1, 2, 0)); // Builder con sconto 2

        assertEquals(2, p.getFoodDiscount(), "I Costruttori dovrebbero essere la fonte di getFoodDiscount(), non i Raccoglitori");
    }

    @Test
    @DisplayName("Idempotenza del calcolo punteggio finale")
    void testCalculateTotalScoreIdempotence() {
        Player p = newPlayer("Test");
        give(p, new Inventor(1, 1, InventorIcon.FISHHOOK));
        give(p, new Inventor(2, 1, InventorIcon.FLUTE));

        int firstCall = p.calculateTotalScore();
        int secondCall = p.calculateTotalScore();
        int thirdCall = p.calculateTotalScore();

        // Essendo calculateTotalScore chiamato a fine partita o per le leaderboard,
        // l'uso di stream e Set passati per riferimento non deve alterare lo stato interno a ogni chiamata.
        assertEquals(firstCall, secondCall, "Chiamate multiple a calculateTotalScore alterano il risultato");
        assertEquals(firstCall, thirdCall, "Lo stato del calcolo non è puro");
    }

    @Test
    @DisplayName("Calcolo Inventori: Interazione complessa di set e icone")
    void testComplexInventorIconMath() {
        Player p = newPlayer("Test");

        // 4 Inventori, ma solo 2 icone diverse.
        give(p, new Inventor(1, 1, InventorIcon.FISHHOOK));
        give(p, new Inventor(2, 1, InventorIcon.FISHHOOK));
        give(p, new Inventor(3, 1, InventorIcon.FISHHOOK));
        give(p, new Inventor(4, 1, InventorIcon.LEATHER));

        // Formula: Numero totale di Inventori (4) * Numero di icone diverse (2) = 8.
        assertEquals(8, p.calculateTotalScore(), "La matematica degli Inventori fallisce con molti duplicati sbilanciati");
    }
}