package it.polimi.ingsw.networkTest.integration;

import it.polimi.ingsw.common.message.server.*;
import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SUMMARY:
 * Verifies network and memory isolation between distinct game rooms, ensuring actions
 * or disconnections in one match do not leak broadcasts or affect other active matches.
 *
 * EXPECTATION:
 * State modifications, messaging updates, and abrupt network crashes occurring inside one
 * virtual game container have no cross-contamination impact on unrelated concurrent lobbies.
 */
public class GameRoomIsolationTest extends NetworkTestBase {

    @Test
    void testConcurrentGamesDoNotInterfere() throws Exception {
        // Setup Game Alpha
        DummyClient alphaHost = new DummyClient("AlphaHost");
        DummyClient alphaGuest = new DummyClient("AlphaGuest");
        alphaHost.proxy.createGame("AlphaHost", 2);
        alphaHost.waitFor(MatchmakingSuccessMessage.class, 2);

        alphaHost.proxy.getAvailableGames();
        String gameIdAlpha = alphaHost.waitFor(AvailableGamesResponseMessage.class, 2).games().get(0).getGameId();
        alphaGuest.proxy.joinGame("AlphaGuest", gameIdAlpha);

        // Wait for Game Alpha to start
        FullSyncMessage syncAlpha = alphaHost.waitFor(FullSyncMessage.class, 3);
        alphaGuest.waitFor(FullSyncMessage.class, 3);

        String activeAlpha = syncAlpha.activePlayer();
        DummyClient firstAlpha = activeAlpha.equals("AlphaHost") ? alphaHost : alphaGuest;
        DummyClient secondAlpha = activeAlpha.equals("AlphaHost") ? alphaGuest : alphaHost;

        // Setup Game Beta
        DummyClient betaHost = new DummyClient("BetaHost");
        DummyClient betaGuest = new DummyClient("BetaGuest");
        betaHost.proxy.createGame("BetaHost", 2);
        betaHost.waitFor(MatchmakingSuccessMessage.class, 2);

        betaHost.proxy.getAvailableGames();
        String gameIdBeta = betaHost.waitFor(AvailableGamesResponseMessage.class, 2).games().get(0).getGameId();
        betaGuest.proxy.joinGame("BetaGuest", gameIdBeta);

        betaHost.waitFor(FullSyncMessage.class, 3);
        betaGuest.waitFor(FullSyncMessage.class, 3);

        // Action: firstAlpha places a totem in Game Alpha
        firstAlpha.proxy.placeTotem(0);

        // Verification 1: Game Alpha receives DeltaEvent
        assertNotNull(firstAlpha.waitFor(DeltaEventMessage.class, 2));
        assertNotNull(secondAlpha.waitFor(DeltaEventMessage.class, 2));

        // Action 2: BetaGuest abruptly disconnects
        betaGuest.disconnect();

        // Verification 3: BetaHost must receive GameAbortedMessage
        GameAbortedMessage betaAbort = betaHost.waitFor(GameAbortedMessage.class, 3);
        assertNotNull(betaAbort, "BetaHost should receive an abort message due to BetaGuest's disconnection.");

        // Verification 4: Game Alpha is entirely unaffected by Game Beta's crash.
        secondAlpha.proxy.placeTotem(1);
        DeltaEventMessage alphaDelta = secondAlpha.waitFor(DeltaEventMessage.class, 2);
        assertNotNull(alphaDelta, "Game Alpha should continue flawlessly despite Game Beta crashing.");
    }
}