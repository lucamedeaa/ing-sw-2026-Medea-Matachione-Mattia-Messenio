package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.character.Builder;
import it.polimi.ingsw.server.model.card.character.Collector;
import it.polimi.ingsw.server.model.card.character.Inventor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerEdgeCasesTest extends ModelTest {

    @Test
    @DisplayName("Gestione Deficit: Transizione da cibo positivo a negativo")
    void testAddFoodPartialDeficit() {
        Player p = newPlayer("Test");
        p.addPrestige(10);
        p.addFood(2);
        p.addFood(-5);
        assertEquals(0, p.getFood(), "Il cibo non deve mai scendere sotto zero");
        assertEquals(4, p.getPrestigePoints(), "Da 10 PP deve scendere a 4 PP (penalità di -6)");
    }

    @Test
    @DisplayName("Rischio OOP: Doppio conteggio dello sconto Sostentamento")
    void testCollectorSustenanceDiscountLogic() {
        Player p = newPlayer("Test");
        give(p, new Collector(35));
        assertEquals(3, p.getSustenanceDiscount());
    }

    @Test
    @DisplayName("Consistenza Architetturale: Builder contro Collector per FoodDiscount")
    void testCorrectDiscountDelegation() {
        Player p = newPlayer("Test");
        give(p, new Builder(1));
        assertEquals(1, p.getFoodDiscount());
    }

    @Test
    @DisplayName("Idempotenza del calcolo punteggio finale")
    void testCalculateTotalScoreIdempotence() {
        Player p = newPlayer("Test");
        give(p, new Inventor(39));
        give(p, new Inventor(40));

        int firstCall = p.calculateTotalScore();
        int secondCall = p.calculateTotalScore();
        int thirdCall = p.calculateTotalScore();

        assertEquals(firstCall, secondCall);
        assertEquals(firstCall, thirdCall);
    }

    @Test
    @DisplayName("Calcolo Inventori: Interazione complessa di set e icone")
    void testComplexInventorIconMath() {
        Player p = newPlayer("Test");
        give(p, new Inventor(39)); // SPEARHEAD
        give(p, new Inventor(40)); // LEATHER
        give(p, new Inventor(41)); // BREAD
        give(p, new Inventor(42)); // CANOE

        // 4 Inventori * 4 icone diverse = 16
        assertEquals(16, p.calculateTotalScore());
    }
}