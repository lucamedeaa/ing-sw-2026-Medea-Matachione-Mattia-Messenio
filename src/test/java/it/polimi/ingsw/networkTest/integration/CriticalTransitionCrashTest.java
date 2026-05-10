/*
 * Goal: Verify that the server handles disconnections during critical state transitions.
 * Specifically, it simulates a player leaving the game exactly when the turn should end,
 * ensuring the RoomConnectionHandler doesn't attempt to broadcast to a null proxy
 * or leave the ModelController in a locked/deadlocked state.
 */
package it.polimi.ingsw.networkTest.integration;

import it.polimi.ingsw.common.message.server.*;
import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.Test;

public class CriticalTransitionCrashTest extends NetworkTestBase {

    @Test
    void testDisconnectDuringPhaseTransition() throws Exception {
        DummyClient c1 = new DummyClient("Alice");
        DummyClient c2 = new DummyClient("Bob");

        c1.proxy.createGame("Alice", 2);
        c1.waitFor(MatchmakingSuccessMessage.class, 2);
        String gid = gameManager.getAvailableGames().get(0).getGameId();
        c2.proxy.joinGame("Bob", gid);

        FullSyncMessage sync = c1.waitFor(FullSyncMessage.class, 2);
        c2.waitFor(FullSyncMessage.class, 2);

        String active = sync.activePlayer();
        DummyClient first = active.equals("Alice") ? c1 : c2;
        DummyClient second = active.equals("Alice") ? c2 : c1;

        // Tile 0 (Template B per 2 player) e Tile 1 (Template C)
        first.proxy.placeTotem(0);
        first.waitFor(DeltaEventMessage.class, 2);
        second.waitFor(DeltaEventMessage.class, 2);

        second.proxy.placeTotem(1);
        first.waitFor(DeltaEventMessage.class, 2);
        second.waitFor(DeltaEventMessage.class, 2);

        // Now in ActionState. first takes a card and dies.
        // Ha posizionato su 0 (Template B: 1 presa in basso), quindi deve prendere da row 1
        first.proxy.takeCard(1, 0);
        first.disconnect();

        // second must receive GameAbortedMessage
        second.waitFor(GameAbortedMessage.class, 5);
    }
}