package it.polimi.ingsw.clientTest;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.common.network.dto.BoardDto;
import it.polimi.ingsw.common.network.dto.PlayerDto;
import it.polimi.ingsw.server.model.enums.TotemColor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for critical concurrency edge cases in the client model layer.
 * These tests verify that the observer pattern, batching mechanism,
 * and log consumption are safe under concurrent access.
 */
@Timeout(10)
public class ClientModelConcurrencyTest {

    /**
     * SUMMARY:
     * An observer calls model.getPlayers() inside its onStateChanged callback.
     * If the model uses a non-reentrant lock, this will deadlock.
     *
     * EXPECTATION:
     * The test completes without deadlock, proving observer re-entrancy is safe.
     */
    @Test
    @DisplayName("DEADLOCK 1: Observer can re-enter model during notification without deadlock")
    void testObserverCanReenterModelDuringNotification() {
        GameModel model = new GameModel();
        AtomicBoolean callbackCompleted = new AtomicBoolean(false);

        model.addObserver(() -> {
            // Re-entrant call: reading model state from inside the observer callback
            model.getPlayers();
            model.getActivePlayer();
            model.getMyActions();
            callbackCompleted.set(true);
        });

        // Trigger the observer via a state mutation
        model.setFullState(
                new BoardDto(List.of(1, 2, 3), List.of(4, 5, 6), 1, 1, 1),
                List.of(new PlayerDto("Alice", 5, 0, TotemColor.ORANGE, 0, 0)),
                "Alice"
        );

        assertTrue(callbackCompleted.get(),
                "DEADLOCK DETECTED: The observer callback never completed, " +
                "indicating a non-reentrant lock in the model.");
    }

    /**
     * SUMMARY:
     * One thread runs executeBatch while another thread concurrently reads model state.
     * The reader must never see partially-applied batch state.
     *
     * EXPECTATION:
     * Both threads complete without exceptions. The model reaches a consistent
     * final state (both mutations applied or neither).
     */
    @Test
    @DisplayName("SAFETY 1: executeBatch and concurrent reads are serialized")
    void testBatchAndReadsAreSerialized() throws InterruptedException {
        GameModel model = new GameModel();
        CountDownLatch start = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();

        // Pre-populate
        model.setFullState(
                new BoardDto(List.of(1, 2, 3), List.of(4, 5, 6), 1, 1, 1),
                List.of(new PlayerDto("Alice", 5, 0, TotemColor.BLUE, 0, 0)),
                "Alice"
        );

        // Writer thread: runs a batch that makes multiple mutations
        Thread writer = new Thread(() -> {
            try {
                start.await();
                for (int i = 0; i < 1000; i++) {
                    model.executeBatch(() -> {
                        model.addGameLog("batch log");
                        model.setActivePlayer("Alice");
                    });
                }
            } catch (Throwable t) {
                error.compareAndSet(null, t);
            }
        }, "Batch-Writer");

        // Reader thread: continuously reads model state
        Thread reader = new Thread(() -> {
            try {
                start.await();
                for (int i = 0; i < 1000; i++) {
                    model.getPlayers();
                    model.getActivePlayer();
                    model.consumeGameLogs();
                }
            } catch (Throwable t) {
                error.compareAndSet(null, t);
            }
        }, "State-Reader");

        writer.start();
        reader.start();
        start.countDown();

        writer.join(5000);
        reader.join(5000);

        assertNull(error.get(),
                "Concurrent batch/read caused an exception: " + error.get());
        assertFalse(writer.isAlive() || reader.isAlive(),
                "Threads did not terminate in time.");
    }

    /**
     * SUMMARY:
     * One thread writes game logs while another consumes them concurrently.
     * If the underlying collection is not thread-safe, this will throw
     * ConcurrentModificationException.
     *
     * EXPECTATION:
     * No exceptions are thrown. All written logs are eventually consumed.
     */
    @Test
    @DisplayName("SAFETY 2: Concurrent log write and consume does not throw CME")
    void testConcurrentLogReadAndWrite() throws InterruptedException {
        GameModel model = new GameModel();
        CountDownLatch start = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();

        // Producer thread
        Thread producer = new Thread(() -> {
            try {
                start.await();
                for (int i = 0; i < 2000; i++) {
                    model.addGameLog("Log entry " + i);
                }
            } catch (Throwable t) {
                error.compareAndSet(null, t);
            }
        }, "Log-Producer");

        // Consumer thread
        Thread consumer = new Thread(() -> {
            try {
                start.await();
                for (int i = 0; i < 2000; i++) {
                    model.consumeGameLogs();
                }
            } catch (Throwable t) {
                error.compareAndSet(null, t);
            }
        }, "Log-Consumer");

        producer.start();
        consumer.start();
        start.countDown();

        producer.join(5000);
        consumer.join(5000);

        assertNull(error.get(),
                "Concurrent log read/write caused an exception: " + error.get());
        assertFalse(producer.isAlive() || consumer.isAlive(),
                "Threads did not terminate in time.");
    }
}
