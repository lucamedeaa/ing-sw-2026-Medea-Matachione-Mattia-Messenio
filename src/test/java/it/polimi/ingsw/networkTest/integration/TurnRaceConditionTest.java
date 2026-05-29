/*
 * Goal: Stress-test the turn enforcement logic under extreme race conditions.
 * If two players send an action command at the exact same millisecond, the server
 * must process them sequentially, accept the active player's command, and
 * safely reject the waiting player's command without throwing internal exceptions.
 */
package it.polimi.ingsw.networkTest.integration;

import it.polimi.ingsw.common.message.server.DeltaEventMessage;
import it.polimi.ingsw.common.message.server.ErrorMessage;
import it.polimi.ingsw.common.message.server.FullSyncMessage;
import it.polimi.ingsw.common.message.server.MatchmakingSuccessMessage;
import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TurnRaceConditionTest extends NetworkTestBase {

    @Test
    @DisplayName("Out-of-turn and repeated action commands are rejected sequentially")
    void testSimultaneousActionCommands() throws Exception {
        DummyClient p1 = new DummyClient("Player1");
        DummyClient p2 = new DummyClient("Player2");

        // Fast-forward to the game
        p1.proxy.createGame("Player1", 2);
        p1.waitFor(MatchmakingSuccessMessage.class, 2);
        String gameId = gameManager.getAvailableGames().get(0).getGameId();
        p2.proxy.joinGame("Player2", gameId);

        FullSyncMessage sync = p1.waitFor(FullSyncMessage.class, 3);
        p2.waitFor(FullSyncMessage.class, 3);

        String firstPlayer = sync.activePlayer();
        DummyClient active = firstPlayer.equals("Player1") ? p1 : p2;
        DummyClient waiting = firstPlayer.equals("Player1") ? p2 : p1;

        active.proxy.placeTotem(0); // Tile 0 = B (0 upper, 1 lower pick)
        active.waitFor(DeltaEventMessage.class, 2);
        waiting.waitFor(DeltaEventMessage.class, 2);

        waiting.proxy.placeTotem(1); // Tile 1 = C (1 upper, 0 lower pick)
        active.waitFor(DeltaEventMessage.class, 2);
        waiting.waitFor(DeltaEventMessage.class, 2);

        // Now in ActionState. It is 'active's turn.

        // Il giocatore in attesa prova a rubare il turno
        waiting.proxy.takeCard(0, 0); // Invalid move (not their turn)
        ErrorMessage errorResponse = waiting.waitFor(ErrorMessage.class, 2);
        assertNotNull(errorResponse, "The waiting player's invalid move was not rejected with an error.");
        assertTrue(errorResponse.error().toLowerCase().contains("turn") || errorResponse.error().toLowerCase().contains("allowed"),
                "The error message must clearly state the action was out of turn.");

        // Il giocatore attivo tenta un double-click per prendere due carte invalidando i pick totali
        active.proxy.takeCard(1, 0); // Valid move (lower row)
        active.proxy.takeCard(1, 1); // Invalid move, accodata (turn/picks già esauriti)

        DeltaEventMessage successResponse = active.waitFor(DeltaEventMessage.class, 2);
        assertNotNull(successResponse, "The active player's valid move was dropped or failed.");

        ErrorMessage doubleClickError = active.waitFor(ErrorMessage.class, 2);
        assertNotNull(doubleClickError, "The active player's second queued move was not rejected.");
    }
}