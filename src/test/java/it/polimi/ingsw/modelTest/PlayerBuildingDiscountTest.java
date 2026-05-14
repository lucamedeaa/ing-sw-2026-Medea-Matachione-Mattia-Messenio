package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.character.Builder;
import it.polimi.ingsw.server.model.card.character.Collector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerBuildingDiscountTest extends ModelTest {

    @Test
    @DisplayName("Isolamento Sconti: I Raccoglitori non devono scontare gli Edifici")
    void testBuilderAndCollectorDiscountSeparation() {
        Player p = newPlayer("TestPlayer");

        // Aggiungiamo un Costruttore (Builder) che dà 2 di sconto per l'acquisto di Edifici
        give(p, new Builder(2));

        // Aggiungiamo un Raccoglitore (Collector) che dà 3 di sconto SOLO per l'evento Sostentamento
        give(p, new Collector(35));

        // Il metodo getFoodDiscount() (se usato dal controller per i costi degli edifici)
        // DEVE restituire solo 2. Se restituisce 5, il Collector sta "inquinando"
        // il calcolo di acquisto degli edifici.
        assertEquals(2, p.getFoodDiscount(),
            "Lo sconto per l'acquisto degli edifici (getFoodDiscount) deve contare solo i Costruttori. Hai incluso erroneamente i Raccoglitori.");

        // Il sostentamento deve ricevere lo sconto corretto, ignorando il Costruttore
        assertEquals(3, p.getSustenanceDiscount(),
            "Lo sconto del sostentamento (getSustenanceDiscount) deve contare solo i Raccoglitori. Hai incluso erroneamente i Costruttori o applicato doppi conteggi.");
    }
}