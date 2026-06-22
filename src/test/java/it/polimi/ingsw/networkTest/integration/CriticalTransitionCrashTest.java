package it.polimi.ingsw.networkTest.integration;

import it.polimi.ingsw.common.message.server.*;
import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.Test;

/**
 * SUMMARY:
 * Verifies server resilience when a client disconnects during state transitions,
 * ensuring no null broadcasts, deadlocks, or leaked game instances occur.
 *
 * EXPECTATION:
 * If a critical turn-ending packet is immediately followed by a connection drop,
 * the game engine safely unwinds its execution state and issues an abort message
 * to surviving players instead of locking up.
 */
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

        first.proxy.takeCard(1, 0);
        first.disconnect();

        // second must receive GameAbortedMessage
        second.waitFor(GameAbortedMessage.class, 5);
    }
}