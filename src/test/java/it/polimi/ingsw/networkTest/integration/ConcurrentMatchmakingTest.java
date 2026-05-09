/*
 * Goal: Stress test the LobbyController and GameManager by simulating a "thundering herd"
 * of clients attempting to join a nearly full game simultaneously.
 * It verifies that the server correctly enforces the maxPlayers limit under high concurrency
 * and does not allow the room size to exceed its capacity or cause inconsistent states.
 */
package it.polimi.ingsw.networkTest.integration;

import it.polimi.ingsw.common.message.server.ErrorMessage;
import it.polimi.ingsw.common.message.server.MatchmakingSuccessMessage;
import it.polimi.ingsw.common.message.server.ServerMessage;
import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.Test;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ConcurrentMatchmakingTest extends NetworkTestBase {

    @Test
    void testConcurrentJoinBurst() throws Exception {
        int capacity = 2;
        int attackers = 10;

        //  Create a game
        DummyClient host = new DummyClient("Host");
        host.proxy.createGame("Host", capacity);
        host.waitFor(MatchmakingSuccessMessage.class, 2);
        String gameId = gameManager.getAvailableGames().get(0).getGameId();

        //  Launch 10 clients at the same time
        ExecutorService burst = Executors.newFixedThreadPool(attackers);
        CountDownLatch starterGun = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < attackers; i++) {
            final int id = i;
            burst.submit(() -> {
                try {
                    DummyClient guest = new DummyClient("Guest_" + id);
                    starterGun.await(); // Sync start
                    guest.proxy.joinGame("Guest_" + id, gameId);

                    // Check if they got in
                    Object response = guest.waitFor(ServerMessage.class, 2); // Simple wait
                    if (response instanceof MatchmakingSuccessMessage) successCount.incrementAndGet();
                } catch (Exception ignored) {}
            });
        }

        starterGun.countDown();
        burst.shutdown();
        burst.awaitTermination(5, TimeUnit.SECONDS);

        // Host already in + SuccessCount should be Exactly Capacity (2)
        // So only 1 guest should have succeeded.
        assertEquals(1, successCount.get(), "Only one guest should have been admitted to a 2-player game.");
    }
}