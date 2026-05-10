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

        c1.waitFor(FullSyncMessage.class, 2);
        c2.waitFor(FullSyncMessage.class, 2);

        // Simulating rapid fire: Take last card and DISCONNECT instantly
        // This hits the SocketClientHandler thread while the GameExecutor is processing the turn.
        c1.proxy.placeTotem(0);
        c1.waitFor(DeltaEventMessage.class, 2);

        c2.proxy.placeTotem(1);
        c2.waitFor(DeltaEventMessage.class, 2);

        // Now in ActionState. C1 takes a card and dies.
        c1.proxy.takeCard(0, 0);
        c1.disconnect();

        // Bob (C2) must receive a GameAbortedMessage, not a crash/timeout
        c2.waitFor(GameAbortedMessage.class, 5);
    }
}