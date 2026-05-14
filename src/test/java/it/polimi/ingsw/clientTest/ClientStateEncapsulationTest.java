package it.polimi.ingsw.clientTest;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.snapshot.PlayerSnapshot;
import it.polimi.ingsw.common.network.dto.BoardDto;
import it.polimi.ingsw.common.network.dto.PlayerDto;
import it.polimi.ingsw.server.model.enums.TotemColor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientStateEncapsulationTest {

    @Nested
    @DisplayName("GameModel State Encapsulation")
    class StateEncapsulation {

        @Test
        @DisplayName("getPlayers() returns a deep copy preventing external mutations")
        void testGetPlayersReturnsDeepCopy() {
            GameModel model = new GameModel();

            // Setup dello stato iniziale
            PlayerDto p1 = new PlayerDto("Alice", 5, 0, TotemColor.ORANGE, 0, 0);
            BoardDto emptyBoard = new BoardDto(List.of(), List.of(), 1, 1);
            model.setFullState(emptyBoard, List.of(p1), "Alice");

            // La UI "legge" lo stato (es. inizio del rendering)
            Map<String, PlayerSnapshot> uiSnapshotMap = model.getPlayers();
            PlayerSnapshot aliceSnapshot = uiSnapshotMap.get("Alice");

            // Il Network Thread aggiorna il modello mentre la UI sta elaborando
            model.updatePlayerResources("Alice", 15, 0, 0, 0);

            // Verifica: lo snapshot in mano alla UI DEVE rimanere inalterato.
            // Se fallisce (restituisce 15), significa che hai restituito una Shallow Copy
            // di un oggetto mutabile, esponendo l'app a Data Races.
            assertEquals(5, aliceSnapshot.getFood(),
                    "VULNERABILITA': Il PlayerSnapshot in mano alla UI ha subito una mutazione " +
                            "in background. Devi implementare una Deep Copy o usare record immutabili.");
        }
    }
}