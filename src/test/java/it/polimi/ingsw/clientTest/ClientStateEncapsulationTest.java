package it.polimi.ingsw.clientTest;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.snapshot.PlayerSnapshot;
import it.polimi.ingsw.common.network.dto.BoardDto;
import it.polimi.ingsw.common.network.dto.PlayerDto;
import it.polimi.ingsw.server.model.enums.TotemColor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies that state requests return a deep copy or immutable snapshot,
 * preventing background network threads from inducing data races in the UI.
 */
@Timeout(10)
public class ClientStateEncapsulationTest {

    @Nested
    @DisplayName("GameModel State Encapsulation")
    class StateEncapsulation {

        @Test
        @DisplayName("getPlayers() returns a deep copy preventing external mutations")
        void testGetPlayersReturnsDeepCopy() {
            GameModel model = new GameModel();

            // Setup initial state
            PlayerDto p1 = new PlayerDto("Alice", 5, 0, TotemColor.ORANGE, 0, 0);
            BoardDto emptyBoard = new BoardDto(List.of(), List.of(), 1, 1, 1);
            model.setFullState(emptyBoard, List.of(p1), "Alice");

            // UI reads the state (e.g., at the start of rendering)
            Map<String, PlayerSnapshot> uiSnapshotMap = model.getPlayers();
            PlayerSnapshot aliceSnapshot = uiSnapshotMap.get("Alice");

            // Network Thread updates the model while the UI is processing
            model.updatePlayerResources("Alice", 15, 0, 0, 0);

            // Verification: the snapshot held by the UI MUST remain unaltered.
            // If it fails (returns 15), a Shallow Copy of a mutable object was returned, exposing the application to Data Races.
            assertEquals(5, aliceSnapshot.getFood(),
                    "VULNERABILITY: The PlayerSnapshot held by the UI was mutated in the background. " +
                            "You must implement a Deep Copy or use immutable records.");
        }
    }
}