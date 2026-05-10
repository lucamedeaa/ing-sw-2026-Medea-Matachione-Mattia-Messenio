/*
 * Goal: Stress-test the turn enforcement logic under extreme race conditions.
 * If two players send an action command at the exact same millisecond, the server
 * must process them sequentially, accept the active player's command, and
 * safely reject the waiting player's command without throwing internal exceptions.
 */
package it.polimi.ingsw.networkTest.integration;

import it.polimi.ingsw.common.message.server.DeltaEventMessage;
import it.polimi.ingsw.common.message.server.ErrorMessage;
import it.polimi.ingsw.common.message.server.FullSyncMessage;
import it.polimi.ingsw.common.message.server.MatchmakingSuccessMessage;
import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class TurnRaceConditionTest extends NetworkTestBase {

    @Test
    void testSimultaneousActionCommands() throws Exception {
        DummyClient p1 = new DummyClient("Player1");
        DummyClient p2 = new DummyClient("Player2");

        // Fast-forward to the game
        p1.proxy.createGame("Player1", 2);
        p1.waitFor(MatchmakingSuccessMessage.class, 2);
        String gameId = gameManager.getAvailableGames().get(0).getGameId();
        p2.proxy.joinGame("Player2", gameId);

        FullSyncMessage sync = p1.waitFor(FullSyncMessage.class, 3);
        p2.waitFor(FullSyncMessage.class, 3);

        // Fast-forward through Placement State
        String firstPlayer = sync.activePlayer();
        DummyClient active = firstPlayer.equals("Player1") ? p1 : p2;
        DummyClient waiting = firstPlayer.equals("Player1") ? p2 : p1;

        active.proxy.placeTotem(0);
        active.waitFor(DeltaEventMessage.class, 2);
        waiting.waitFor(DeltaEventMessage.class, 2);

        waiting.proxy.placeTotem(1);
        active.waitFor(DeltaEventMessage.class, 2);
        waiting.waitFor(DeltaEventMessage.class, 2);

        // now in ActionState. It is 'active's turn.

        ExecutorService burstExecutor = Executors.newFixedThreadPool(2);
        CountDownLatch startGun = new CountDownLatch(1);

        // Both clients will fire the 'takeCard' command at the exact same moment
        burstExecutor.submit(() -> {
            try {
                startGun.await();
                active.proxy.takeCard(0, 0); // Valid move
            } catch (Exception ignored) {}
        });

        burstExecutor.submit(() -> {
            try {
                startGun.await();
                waiting.proxy.takeCard(1, 0); // INVALID move (not their turn)
            } catch (Exception ignored) {}
        });

        startGun.countDown();
        burstExecutor.shutdown();
        burstExecutor.awaitTermination(3, TimeUnit.SECONDS);

        // Verifications:
        //  The active player MUST receive a DeltaEvent indicating their move was accepted
        DeltaEventMessage successResponse = active.waitFor(DeltaEventMessage.class, 2);
        assertNotNull(successResponse, "The active player's valid move was dropped or failed.");

        //  The waiting player MUST receive an ErrorMessage for playing out of turn
        ErrorMessage errorResponse = waiting.waitFor(ErrorMessage.class, 2);
        assertNotNull(errorResponse, "The waiting player's invalid move was not rejected with an error.");
        assertTrue(errorResponse.error().toLowerCase().contains("turn") || errorResponse.error().toLowerCase().contains("allowed"),
                "The error message must clearly state the action was out of turn.");
    }
}