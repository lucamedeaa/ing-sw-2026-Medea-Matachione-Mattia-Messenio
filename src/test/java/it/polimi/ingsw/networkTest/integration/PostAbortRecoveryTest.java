package it.polimi.ingsw.networkTest.integration;

import it.polimi.ingsw.common.message.server.FullSyncMessage;
import it.polimi.ingsw.common.message.server.GameAbortedMessage;
import it.polimi.ingsw.common.message.server.MatchmakingSuccessMessage;
import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SUMMARY:
 * Verifies that the network state machine resets correctly post-abort, ensuring surviving
 * clients seamlessly revert to the lobby state and can immediately start new matches.
 *
 * EXPECTATION:
 * When an active match terminates unexpectedly due to a player disconnection, the server
 * rolls back the connection session state of the remaining players to the lobby phase,
 * immediately authorizing new game creation requests.
 */
public class PostAbortRecoveryTest extends NetworkTestBase {

    @Test
    void testSurvivorCanStartNewGameAfterAbort() throws Exception {
        DummyClient quitter = new DummyClient("Quitter");
        DummyClient survivor = new DummyClient("Survivor");

        // Setup a standard 2-player game
        quitter.proxy.createGame("Quitter", 2);
        quitter.waitFor(MatchmakingSuccessMessage.class, 2);

        quitter.proxy.getAvailableGames();
        String gameId = quitter.waitFor(it.polimi.ingsw.common.message.server.AvailableGamesResponseMessage.class, 2)
                .games().get(0).getGameId();

        survivor.proxy.joinGame("Survivor", gameId);

        // Verify game has actually started
        quitter.waitFor(FullSyncMessage.class, 2);
        survivor.waitFor(FullSyncMessage.class, 2);

        //  Quitter pulls the plug
        quitter.disconnect();

        //  Survivor gets notified that the game is dead
        GameAbortedMessage abortMsg = survivor.waitFor(GameAbortedMessage.class, 3);
        assertNotNull(abortMsg, "Survivor did not receive abort notification.");

        // Survivor tries to create a NEW game immediately.
        // If the ConnectionSession is still stuck in InGameConnectionState,
        // this will return an ErrorMessage ("Command not valid...").
        // If recovery worked, it will return MatchmakingSuccessMessage.

        survivor.proxy.createGame("Survivor", 4);
        MatchmakingSuccessMessage newGameMsg = survivor.waitFor(MatchmakingSuccessMessage.class, 3);

        assertNotNull(newGameMsg, "Survivor was unable to create a new game after a crash. State Machine is stuck.");
    }
}