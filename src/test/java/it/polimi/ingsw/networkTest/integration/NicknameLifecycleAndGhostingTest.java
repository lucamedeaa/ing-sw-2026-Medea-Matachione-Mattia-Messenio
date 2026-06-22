package it.polimi.ingsw.networkTest.integration;

import it.polimi.ingsw.common.message.server.ErrorMessage;
import it.polimi.ingsw.common.message.server.GameLeftSuccessMessage;
import it.polimi.ingsw.common.message.server.MatchmakingSuccessMessage;
import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SUMMARY:
 * Verifies nickname lifecycle management, ensuring unique identifiers are locked during
 * active sessions and immediately released for reuse upon disconnection or graceful exit.
 *
 * EXPECTATION:
 * The network registry prevents distinct clients from registering the same nominal identifier
 * concurrently, yet flushes the ownership key instantly when the original session leaves or resets.
 */
public class NicknameLifecycleAndGhostingTest extends NetworkTestBase {

    @Test
    void testNicknameLockingAndReleasing() throws Exception {
        DummyClient originalAlice = new DummyClient("Alice");

        //  Original Alice creates a game
        originalAlice.proxy.createGame("Alice", 2);
        originalAlice.waitFor(MatchmakingSuccessMessage.class, 2);

        //  An impostor tries to join or create a game using the name "Alice"
        DummyClient imposter = new DummyClient("Imposter");
        imposter.proxy.createGame("Alice", 3); // Tries to use "Alice"

        ErrorMessage imposterError = imposter.waitFor(ErrorMessage.class, 2);
        assertNotNull(imposterError, "Server must block imposter from using an active nickname.");
        assertTrue(imposterError.error().toLowerCase().contains("already in use") || imposterError.error().toLowerCase().contains("invalid"));

        //  Original Alice gracefully leaves the lobby
        originalAlice.proxy.leaveGame();
        originalAlice.waitFor(GameLeftSuccessMessage.class, 2);

        //  A new legitimate client wants to use the name "Alice" now that it's free
        DummyClient newAlice = new DummyClient("NewAlice");
        newAlice.proxy.createGame("Alice", 2);

        MatchmakingSuccessMessage newAliceSuccess = newAlice.waitFor(MatchmakingSuccessMessage.class, 2);
        assertNotNull(newAliceSuccess, "Server must release the nickname after a player leaves, allowing it to be reused.");
    }
}