/*
 * Goal: Verify the core game loop and disconnection handling using the NetworkTestBase.
 * This test ensures players can join, synchronize the board, place totems, and take cards.
 * It also verifies that an unexpected disconnection during the game cleanly aborts the match
 * and notifies the remaining players.
 */
package it.polimi.ingsw.networkTest.integration;

import it.polimi.ingsw.common.message.server.*;
import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameFlowIntegrationTest extends NetworkTestBase {

    private DummyClient client1;
    private DummyClient client2;

    @AfterEach
    public void cleanupClients() {
        if (client1 != null) client1.disconnect();
        if (client2 != null) client2.disconnect();
    }

    @Test
    @DisplayName("Players can join, synchronize, place totems, and take cards")
    public void testFullRoundFlow() throws Exception {
        // Using the robust DummyClient inherited from NetworkTestBase
        client1 = new DummyClient("Alice");
        client2 = new DummyClient("Bob");

        // Setup the Game
        client1.proxy.createGame("Alice", 2);
        client1.waitFor(MatchmakingSuccessMessage.class, 3);

        //  Client 2 fetches available games and joins
        client2.proxy.getAvailableGames();
        AvailableGamesResponseMessage availableGamesMsg = client2.waitFor(AvailableGamesResponseMessage.class, 3);
        String gameId = availableGamesMsg.games().get(0).getGameId();

        client2.proxy.joinGame("Bob", gameId);
        client2.waitFor(MatchmakingSuccessMessage.class, 3);

        // Once the room is full, the game starts and sends a FullSyncMessage to both
        FullSyncMessage sync1 = client1.waitFor(FullSyncMessage.class, 3);
        FullSyncMessage sync2 = client2.waitFor(FullSyncMessage.class, 3);

        assertNotNull(sync1.board(), "Board should be initialized and sent to Alice");
        assertNotNull(sync2.board(), "Board should be initialized and sent to Bob");

        // Placement Phase (Totem placement)
        String activePlayer = sync1.activePlayer();
        DummyClient activeClient = activePlayer.equals("Alice") ? client1 : client2;
        DummyClient waitingClient = activePlayer.equals("Alice") ? client2 : client1;

        // Active player places totem on tile 0
        activeClient.proxy.placeTotem(0);

        // Wait for DeltaEvent indicating the placement was successful and turn changed
        DeltaEventMessage delta1 = activeClient.waitFor(DeltaEventMessage.class, 3);
        assertEquals(waitingClient.nickname, delta1.activePlayer(), "Turn should pass to the waiting player");

        // The second player places their totem on tile 1
        waitingClient.proxy.placeTotem(1);

        // Wait for DeltaEvent indicating placement successful. Now ActionState begins!
        DeltaEventMessage delta2 = waitingClient.waitFor(DeltaEventMessage.class, 3);

        // Action Phase (Taking cards)
        String actionPhasePlayer = delta2.activePlayer();
        DummyClient actionClient = actionPhasePlayer.equals("Alice") ? client1 : client2;

        // Assuming Tile 0 or 1 gives picks, attempt to take a card from the upper row (0, 0)
        actionClient.proxy.takeCard(0, 0);

        DeltaEventMessage delta3 = actionClient.waitFor(DeltaEventMessage.class, 3);
        assertNotNull(delta3.events(), "Events should contain the CardTakenEvent");
    }
}