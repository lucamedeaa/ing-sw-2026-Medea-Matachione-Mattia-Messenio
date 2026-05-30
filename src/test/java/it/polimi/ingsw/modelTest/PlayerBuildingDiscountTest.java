package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.character.Builder;
import it.polimi.ingsw.server.model.card.character.Collector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerBuildingDiscountTest extends ModelTest {

    /**
     * SUMMARY:
     * Verifies that the food discount (getFoodDiscount) only counts Builder
     * cards and the sustenance discount (getSustenanceDiscount) only counts
     * Collector cards, ensuring the two discount types are properly isolated.
     *
     * EXPECTATION:
     * getFoodDiscount() returns 2 (from Builder) and getSustenanceDiscount()
     * returns 3 (from Collector), with no cross-contamination.
     */
    @Test
    @DisplayName("Isolamento Sconti: I Raccoglitori non devono scontare gli Edifici")
    void testBuilderAndCollectorDiscountSeparation() {
        Player p = newPlayer("TestPlayer");

        // 2 di sconto per l'acquisto di Edifici
        give(p, new Builder(2));

        // 3 di sconto per l'evento Sostentamento
        give(p, new Collector(35));

        assertEquals(2, p.getFoodDiscount(),
            "Lo sconto per l'acquisto degli edifici (getFoodDiscount) deve contare solo i Costruttori. Hai incluso erroneamente i Raccoglitori.");

        // Il sostentamento deve ricevere lo sconto corretto, ignorando il Costruttore
        assertEquals(3, p.getSustenanceDiscount(),
            "Lo sconto del sostentamento (getSustenanceDiscount) deve contare solo i Raccoglitori. Hai incluso erroneamente i Costruttori o applicato doppi conteggi.");
    }
}