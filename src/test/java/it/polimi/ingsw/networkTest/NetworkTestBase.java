package it.polimi.ingsw.networkTest;

import it.polimi.ingsw.client.network.SocketServerConnection;
import it.polimi.ingsw.client.network.SocketServerProxy;
import it.polimi.ingsw.common.message.server.*;
import it.polimi.ingsw.common.visitor.ClientMessageVisitor;
import it.polimi.ingsw.server.controller.LobbyController;
import it.polimi.ingsw.server.lobby.GameManager;
import it.polimi.ingsw.server.network.handler.SocketClientHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Base class for integration network testing. Sets up an in-memory test server
 * and handles connection cleanup between tests.
 */
public abstract class NetworkTestBase {
    protected int serverPort;
    protected ServerSocket serverSocket;
    protected GameManager gameManager;
    protected LobbyController lobbyController;

    private ExecutorService serverExecutor;
    protected List<Socket> clientSockets = new ArrayList<>();
    protected List<DummyClient> dummyClients = new ArrayList<>();

    @BeforeEach
    void setup() throws IOException {
        gameManager = new GameManager(null);
        lobbyController = new LobbyController(gameManager);

        serverSocket = new ServerSocket(0);
        serverPort = serverSocket.getLocalPort();
        serverExecutor = Executors.newCachedThreadPool();

        new Thread(() -> {
            try {
                while (!serverSocket.isClosed()) {
                    Socket client = serverSocket.accept();
                    serverExecutor.submit(new SocketClientHandler(client, gameManager, lobbyController));
                }
            } catch (IOException ignored) {}
        }).start();
    }

    @AfterEach
    void tearDown() throws IOException {
        for (DummyClient dc : dummyClients) {
            try { dc.disconnect(); } catch (Exception ignored) {}
        }
        dummyClients.clear();

        for (Socket s : clientSockets) {
            if (s != null && !s.isClosed()) {
                s.close();
            }
        }
        clientSockets.clear();

        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        if (serverExecutor != null) {
            serverExecutor.shutdownNow();
        }
    }

    /**
     * Mock client used to simulate player network behavior and capture incoming server packets.
     */
    public class DummyClient implements ClientMessageVisitor {
        public final String nickname;
        public final SocketServerProxy proxy;
        private final SocketServerConnection connection;
        private final CopyOnWriteArrayList<ServerMessage> history = new CopyOnWriteArrayList<>();

        public DummyClient(String nickname) throws IOException {
            this.nickname = nickname;
            this.connection = new SocketServerConnection("localhost", serverPort, this);
            new Thread(connection).start();
            this.proxy = new SocketServerProxy(connection);
            dummyClients.add(this);
        }

        /**
         * Asynchronously polls the internal message history until a specific type of network packet arrives.
         * * @param clazz The expected message class (e.g., MatchmakingSuccessMessage.class)
         * @param timeoutSec Maximum time to wait before failing the test
         * @return The received message downcasted to type T
         */
        public <T extends ServerMessage> T waitFor(Class<T> clazz, int timeoutSec) {
            long end = System.currentTimeMillis() + (timeoutSec * 1000L);

            while (System.currentTimeMillis() < end) {
                for (ServerMessage msg : history) {
                    if (clazz.isInstance(msg)) {
                        history.remove(msg);
                        return clazz.cast(msg);
                    }
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            fail("Timeout: " + clazz.getSimpleName() + " not received for player: " + nickname);
            return null;
        }

        public void disconnect() {
            connection.disconnect();
        }

        @Override public void visit(FullSyncMessage m) { history.add(m); }
        @Override public void visit(DeltaEventMessage m) { history.add(m); }
        @Override public void visit(ErrorMessage m) { history.add(m); }
        @Override public void visit(MatchmakingSuccessMessage m) { history.add(m); }
        @Override public void visit(AvailableGamesResponseMessage m) { history.add(m); }
        @Override public void visit(GameAbortedMessage m) { history.add(m); }
        @Override public void visit(RoomUpdateMessage m) { history.add(m); }
        @Override public void visit(GameLeftSuccessMessage m) { history.add(m); }
        @Override public void visit(GameCompletedMessage m) { history.add(m); }
        @Override public void visit(LeaderboardResponseMessage m) { history.add(m); }
        @Override public void visit(ServerDisconnectedMessage m) { history.add(m); }
        @Override public void visit(it.polimi.ingsw.common.network.dto.event.ErrorDto m) { history.add(new ErrorMessage(m.error())); }
    }
}