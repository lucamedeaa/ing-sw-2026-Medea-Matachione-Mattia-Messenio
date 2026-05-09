/*
 * Goal: Verify that the network state machine resets correctly after a game is forcefully aborted.
 * If Player A disconnects mid-game, Player B receives a GameAbortedMessage.
 * This test ensures that Player B's ConnectionSession is successfully reverted back to
 * LobbyConnectionState, allowing them to immediately create or join a NEW game
 * without having to restart their client.
 */
package it.polimi.ingsw.networkTest.integration;

import it.polimi.ingsw.common.message.server.FullSyncMessage;
import it.polimi.ingsw.common.message.server.GameAbortedMessage;
import it.polimi.ingsw.common.message.server.MatchmakingSuccessMessage;
import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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