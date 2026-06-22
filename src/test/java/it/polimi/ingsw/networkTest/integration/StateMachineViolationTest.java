package it.polimi.ingsw.networkTest.integration;

import it.polimi.ingsw.client.network.SocketServerConnection;
import it.polimi.ingsw.client.network.SocketServerProxy;
import it.polimi.ingsw.common.message.server.ErrorMessage;
import it.polimi.ingsw.common.visitor.ClientMessageVisitor;
import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SUMMARY:
 * Verifies that the server network state machine strictly blocks and rejects out-of-state
 * connection commands, protecting the underlying game loop from illegal phase violations.
 *
 * EXPECTATION:
 * When an unauthenticated or lobby-bound client submits an in-game transaction payload,
 * the network session protocol interceptor traps the message and dispatches a validation error
 * back to the source client, aborting downstream model execution.
 */
public class StateMachineViolationTest extends NetworkTestBase {

    @Test
    @DisplayName("Taking a card while in the lobby returns an ErrorMessage")
    void takingCardInLobbyFails() throws IOException, InterruptedException {
        CountDownLatch errorLatch = new CountDownLatch(1);
        String[] capturedError = new String[1];

        ClientMessageVisitor testVisitor = new ClientMessageVisitor() {
            @Override
            public void visit(ErrorMessage message) {
                capturedError[0] = message.error();
                errorLatch.countDown();
            }

            // Empty implementations for the rest of the interface methods
            @Override public void visit(it.polimi.ingsw.common.message.server.FullSyncMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.DeltaEventMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.network.dto.event.ErrorDto message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.MatchmakingSuccessMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.AvailableGamesResponseMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.GameAbortedMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.RoomUpdateMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.GameLeftSuccessMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.GameCompletedMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.LeaderboardResponseMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.ServerDisconnectedMessage message) {}
        };

        SocketServerConnection connection = new SocketServerConnection("localhost", serverPort, testVisitor);
        new Thread(connection).start();
        SocketServerProxy proxy = new SocketServerProxy(connection);

        // Attempting an in-game action while the session is still in the lobby state
        proxy.takeCard(0, 0);

        // Wait up to 2 seconds for the asynchronous error response
        boolean receivedResponse = errorLatch.await(2, TimeUnit.SECONDS);
        connection.disconnect();

        assertTrue(receivedResponse, "Server did not respond in time.");
        assertEquals("Command not valid in the current connection state.", capturedError[0]);
    }
}