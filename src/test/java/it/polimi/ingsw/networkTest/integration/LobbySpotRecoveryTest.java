package it.polimi.ingsw.networkTest.integration;

import it.polimi.ingsw.common.message.server.AvailableGamesResponseMessage;
import it.polimi.ingsw.common.message.server.FullSyncMessage;
import it.polimi.ingsw.common.message.server.MatchmakingSuccessMessage;
import it.polimi.ingsw.common.message.server.RoomUpdateMessage;
import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SUMMARY:
 * Verifies that pre-game disconnections cleanly purge ghost sessions from lobbies,
 * freeing slots immediately and allowing subsequent clients to join and start the match.
 *
 * EXPECTATION:
 * When a lobby player abruptly drops their socket connection before a match begins,
 * the server reclaims their allocated slot, broadcasts a room status update to survivors,
 * and successfully fills the vacant position on subsequent join attempts.
 */
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

        // Wait for the server to process the disconnect and notify the host
        host.waitFor(RoomUpdateMessage.class, 3);

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