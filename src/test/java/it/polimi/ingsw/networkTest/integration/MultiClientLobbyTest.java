package it.polimi.ingsw.networkTest.integration;

import it.polimi.ingsw.client.network.SocketServerConnection;
import it.polimi.ingsw.client.network.SocketServerProxy;
import it.polimi.ingsw.common.message.server.AvailableGamesResponseMessage;
import it.polimi.ingsw.common.message.server.MatchmakingSuccessMessage;
import it.polimi.ingsw.common.message.server.RoomUpdateMessage;
import it.polimi.ingsw.common.visitor.ClientMessageVisitor;
import it.polimi.ingsw.networkTest.NetworkTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/*Verificare la concorrenza e il broadcasting. Se un client crea una stanza, un secondo client che entra nella stessa stanza deve scatenare un RoomUpdateMessage per entrambi.
Questo dimostra che il ConnectionSession e il GameRoom comunicano correttamente attraverso i thread separati dei due client.*/

public class MultiClientLobbyTest extends NetworkTestBase {

    @Test
    @DisplayName("Multiple clients interact and receive broadcasts in the lobby")
    void testMultiClientLobbyInteraction() throws IOException, InterruptedException {
        CountDownLatch hostSuccessLatch = new CountDownLatch(1);
        CountDownLatch guestListLatch = new CountDownLatch(1);
        CountDownLatch hostRoomUpdateLatch = new CountDownLatch(1);
        String[] fetchedGameId = new String[1];

        ClientMessageVisitor hostVisitor = new ClientMessageVisitor() {
            @Override
            public void visit(MatchmakingSuccessMessage message) {
                hostSuccessLatch.countDown();
            }

            @Override
            public void visit(RoomUpdateMessage message) {
                if (message.currentPlayers().contains("GuestPlayer")) {
                    hostRoomUpdateLatch.countDown();
                }
            }

            @Override public void visit(it.polimi.ingsw.common.message.server.FullSyncMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.DeltaEventMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.network.dto.event.ErrorDto message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.ErrorMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.AvailableGamesResponseMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.GameAbortedMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.GameLeftSuccessMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.GameCompletedMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.LeaderboardResponseMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.ServerDisconnectedMessage message) {}
        };

        ClientMessageVisitor guestVisitor = new ClientMessageVisitor() {
            @Override
            public void visit(AvailableGamesResponseMessage message) {
                if (!message.games().isEmpty()) {
                    fetchedGameId[0] = message.games().get(0).getGameId();
                }
                guestListLatch.countDown();
            }

            @Override public void visit(it.polimi.ingsw.common.message.server.FullSyncMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.DeltaEventMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.network.dto.event.ErrorDto message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.ErrorMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.MatchmakingSuccessMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.GameAbortedMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.RoomUpdateMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.GameLeftSuccessMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.GameCompletedMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.LeaderboardResponseMessage message) {}
            @Override public void visit(it.polimi.ingsw.common.message.server.ServerDisconnectedMessage message) {}
        };

        SocketServerConnection hostConnection = new SocketServerConnection("localhost", serverPort, hostVisitor);
        new Thread(hostConnection).start();
        SocketServerProxy hostProxy = new SocketServerProxy(hostConnection);

        hostProxy.createGame("HostPlayer", 2);
        assertTrue(hostSuccessLatch.await(2, TimeUnit.SECONDS), "Host did not receive MatchmakingSuccessMessage in time.");

        SocketServerConnection guestConnection = new SocketServerConnection("localhost", serverPort, guestVisitor);
        new Thread(guestConnection).start();
        SocketServerProxy guestProxy = new SocketServerProxy(guestConnection);

        guestProxy.getAvailableGames();
        assertTrue(guestListLatch.await(2, TimeUnit.SECONDS), "Guest did not receive AvailableGamesResponseMessage in time.");
        assertNotNull(fetchedGameId[0], "The created game should be visible in the available games list.");

        guestProxy.joinGame("GuestPlayer", fetchedGameId[0]);

        assertTrue(hostRoomUpdateLatch.await(2, TimeUnit.SECONDS), "Host did not receive RoomUpdateMessage broadcast after guest joined.");

        hostConnection.disconnect();
        guestConnection.disconnect();
    }
}