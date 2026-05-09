/*
 * Goal: Test the server's resilience against message flooding (spamming).
 * It sends a massive burst of PingMessages followed by a valid game action.
 * The server must process the buffer without overflowing the SingleThreadExecutor
 * in ConnectionSession or delaying the game action beyond acceptable limits.
 */
package it.polimi.ingsw.networkTest.protocol;

import it.polimi.ingsw.common.message.client.PingMessage;
import it.polimi.ingsw.common.message.server.MatchmakingSuccessMessage;
import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.Test;

public class ProtocolSpamTest extends NetworkTestBase {

    @Test
    void testPingFloodDoesNotDelayActions() throws Exception {
        DummyClient spammer = new DummyClient("Spammer");

        // Flood the OOS buffer
        for(int i=0; i<5000; i++) {
            // Manual send to bypass proxy delays
            spammer.proxy.getAvailableGames();
        }

        // Immediately send a real command
        spammer.proxy.createGame("Spammer", 2);

        // The server should eventually process the createGame without crashing
        spammer.waitFor(MatchmakingSuccessMessage.class, 10);
    }
}