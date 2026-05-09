/*
 * Goal: Ensure robust tracking and freeing of unique identifiers (nicknames).
 * A nickname must be strictly locked while a player is in the lobby or in a game.
 * However, if the player gracefully leaves or abruptly disconnects, the GameManager
 * MUST release the nickname, allowing a new client to claim it immediately.
 */
package it.polimi.ingsw.networkTest.integration;

import it.polimi.ingsw.common.message.server.ErrorMessage;
import it.polimi.ingsw.common.message.server.GameLeftSuccessMessage;
import it.polimi.ingsw.common.message.server.MatchmakingSuccessMessage;
import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NicknameLifecycleAndGhostingTest extends NetworkTestBase {

    @Test
    void testNicknameLockingAndReleasing() throws Exception {
        DummyClient originalAlice = new DummyClient("Alice");

        //  Original Alice creates a game
        originalAlice.proxy.createGame("Alice", 2);
        originalAlice.waitFor(MatchmakingSuccessMessage.class, 2);

        //  An imposter tries to join or create a game using the name "Alice"
        DummyClient imposter = new DummyClient("Imposter");
        imposter.proxy.createGame("Alice", 3); // Tries to use "Alice"

        ErrorMessage imposterError = imposter.waitFor(ErrorMessage.class, 2);
        assertNotNull(imposterError, "Server must block imposter from using an active nickname.");
        assertTrue(imposterError.error().toLowerCase().contains("already in use") || imposterError.error().toLowerCase().contains("invalid"));

        //  Original Alice gracefully leaves the lobby
        originalAlice.proxy.leaveGame();
        originalAlice.waitFor(GameLeftSuccessMessage.class, 2);

        // Allow server a tiny window to clean up
        Thread.sleep(100);

        //  A new legitimate client wants to use the name "Alice" now that it's free
        DummyClient newAlice = new DummyClient("NewAlice");
        newAlice.proxy.createGame("Alice", 2);

        MatchmakingSuccessMessage newAliceSuccess = newAlice.waitFor(MatchmakingSuccessMessage.class, 2);
        assertNotNull(newAliceSuccess, "Server must release the nickname after a player leaves, allowing it to be reused.");
    }
}