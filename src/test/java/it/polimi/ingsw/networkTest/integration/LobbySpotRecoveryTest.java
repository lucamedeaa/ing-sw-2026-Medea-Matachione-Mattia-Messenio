/*
 * Goal: Verify that if a player disconnects abruptly while waiting in a game lobby
 * (before the game starts), the server correctly cleans up their "ghost" session.
 * Specifically, it ensures that the disconnected player is removed from the GameRoom,
 * freeing up their slot so that another player can join and successfully start the game.
 */
package it.polimi.ingsw.networkTest.integration;

import it.polimi.ingsw.common.message.server.AvailableGamesResponseMessage;
import it.polimi.ingsw.common.message.server.FullSyncMessage;
import it.polimi.ingsw.common.message.server.MatchmakingSuccessMessage;
import it.polimi.ingsw.common.message.server.RoomUpdateMessage;
import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LobbySpotRecoveryTest extends NetworkTestBase {

    @Test
    void testDisconnectedPlayerFreesLobbySlot() throws Exception {
        DummyClient host = new DummyClient("Host");
        DummyClient ghost = new DummyClient("Ghost");
        DummyClient savior = new DummyClient("Savior");

        //  Host creates a 2-player game
        host.proxy.createGame("Host", 2);
        host.waitFor(MatchmakingSuccessMessage.class, 2);

        host.proxy.getAvailableGames();
        AvailableGamesResponseMessage gamesMsg = host.waitFor(AvailableGamesResponseMessage.class, 2);
        String gameId = gamesMsg.games().get(0).getGameId();

        //  Ghost joins the game. The room is now full, triggering the game start.
        // Wait, if it's 2 players, joining starts it immediately. 
        // Let's test a 3-player lobby to test pure lobby recovery.
        host.disconnect(); // Reset state

        host = new DummyClient("Host");
        host.proxy.createGame("Host", 3);
        host.waitFor(MatchmakingSuccessMessage.class, 2);
        host.proxy.getAvailableGames();
        String gameId3 = host.waitFor(AvailableGamesResponseMessage.class, 2).games().get(0).getGameId();

        // Ghost joins (1/3 -> 2/3)
        ghost.proxy.joinGame("Ghost", gameId3);
        ghost.waitFor(MatchmakingSuccessMessage.class, 2);
        host.waitFor(RoomUpdateMessage.class, 2); // Host sees Ghost join

        //  Ghost disconnects abruptly (pulls the plug)
        ghost.disconnect();

        // Wait a moment for the server to process the EOFException and trigger handleClientDisconnection
        Thread.sleep(500);

        // Savior joins (Should be 2/3 now, because Ghost was removed)
        savior.proxy.joinGame("Savior", gameId3);
        savior.waitFor(MatchmakingSuccessMessage.class, 2);

        //  Player 4 joins to fill the final spot
        DummyClient p4 = new DummyClient("Player4");
        p4.proxy.joinGame("Player4", gameId3);

        // Verification: The game should successfully start (FullSync emitted)
        FullSyncMessage syncHost = host.waitFor(FullSyncMessage.class, 3);
        FullSyncMessage syncSavior = savior.waitFor(FullSyncMessage.class, 3);
        FullSyncMessage syncP4 = p4.waitFor(FullSyncMessage.class, 3);

        assertNotNull(syncHost, "Game did not start; ghost player likely locked the room size.");
        assertNotNull(syncSavior);
        assertNotNull(syncP4);
    }
}