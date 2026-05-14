package it.polimi.ingsw.clientTest;

import it.polimi.ingsw.client.model.LobbyModel;
import it.polimi.ingsw.common.network.dto.GameInfoDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LobbyModelConcurrencyTest {

    @Nested
    @DisplayName("LobbyModel Concurrency Safety")
    class Concurrency {

        @Test
        @DisplayName("Concurrent reads and writes on AvailableGames do not throw CME")
        void testAvailableGamesGetterIsThreadSafe() throws InterruptedException {
            LobbyModel lobby = new LobbyModel();
            AtomicReference<Exception> exceptionCaught = new AtomicReference<>();
            CountDownLatch latch = new CountDownLatch(1);

            // Thread 1: Simula la rete che invia continui aggiornamenti
            Thread networkThread = new Thread(() -> {
                try {
                    latch.await();
                    for (int i = 0; i < 2000; i++) {
                        lobby.setAvailableGames(List.of(
                                new GameInfoDto("ID-" + i, "Host", 1, 4),
                                new GameInfoDto("ID-X", "Host2", 2, 3)
                        ));
                    }
                } catch (Exception e) {
                    exceptionCaught.compareAndSet(null, e);
                }
            });

            // Thread 2: Simula la UI che fa poll per renderizzare lo schermo
            Thread uiThread = new Thread(() -> {
                try {
                    latch.await();
                    for (int i = 0; i < 2000; i++) {
                        // Se manca il readLock nel getter, il costruttore di ArrayList
                        // itererà su una collezione in fase di modifica lanciando un'eccezione
                        List<GameInfoDto> games = lobby.getAvailableGames();
                    }
                } catch (Exception e) {
                    exceptionCaught.compareAndSet(null, e);
                }
            });

            networkThread.start();
            uiThread.start();

            // Fai partire i thread simultaneamente
            latch.countDown();

            networkThread.join(3000);
            uiThread.join(3000);

            // Verifica: Nessuna ConcurrentModificationException deve essere lanciata
            assertNull(exceptionCaught.get(),
                    "CRASH RILEVATO: Manca il blocco getReadLock().lock() in getAvailableGames(). " +
                            "Eccezione: " + exceptionCaught.get());
        }
    }
}