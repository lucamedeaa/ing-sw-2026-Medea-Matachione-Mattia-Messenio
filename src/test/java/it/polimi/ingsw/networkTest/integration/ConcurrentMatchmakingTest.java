package it.polimi.ingsw.networkTest.integration;

import it.polimi.ingsw.common.message.server.MatchmakingSuccessMessage;
import it.polimi.ingsw.common.message.server.ServerMessage;
import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.Test;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SUMMARY:
 * Verifies that max capacity limits are strictly enforced under high concurrency,
 * preventing race conditions during a simultaneous client connection burst.
 *
 * EXPECTATION:
 * The server securely serializes incoming join requests, admitting only the exact
 * number of clients required to reach capacity while rejecting any excess requests.
 */
public class ConcurrentMatchmakingTest extends NetworkTestBase {

    @Test
    void testConcurrentJoinBurst() throws Exception {
        int capacity = 2;
        int attackers = 10;

        DummyClient host = new DummyClient("Host");
        host.proxy.createGame("Host", capacity);
        host.waitFor(MatchmakingSuccessMessage.class, 2);
        String gameId = gameManager.getAvailableGames().get(0).getGameId();

        ExecutorService burst = Executors.newFixedThreadPool(attackers);
        CountDownLatch starterGun = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicReference<Throwable> firstError = new AtomicReference<>();

        for (int i = 0; i < attackers; i++) {
            final int id = i;
            burst.submit(() -> {
                try {
                    DummyClient guest = new DummyClient("Guest_" + id);
                    starterGun.await(); // Sync start
                    guest.proxy.joinGame("Guest_" + id, gameId);

                    // Check if they got in
                    Object response = guest.waitFor(ServerMessage.class, 2);
                    if (response instanceof MatchmakingSuccessMessage) successCount.incrementAndGet();
                } catch (Throwable t) {
                    firstError.compareAndSet(null, t);
                }
            });
        }

        starterGun.countDown();
        burst.shutdown();
        burst.awaitTermination(10, TimeUnit.SECONDS);

        // Rethrow if any burst thread failed unexpectedly
        if (firstError.get() != null) {
            fail("Burst thread failed: " + firstError.get().getMessage());
        }

        // Host already in + SuccessCount should be Exactly Capacity (2)
        // So only 1 guest should have succeeded.
        assertEquals(1, successCount.get(), "Only one guest should have been admitted to a 2-player game.");
    }
}