/* * Goal: Provide a robust testing framework for network integration tests.
 * It manages a local server instance with dynamic port assignment and
 * provides a non-destructive DummyClient that stores all incoming server messages
 * in a thread-safe buffer to avoid race conditions during assertions.
 */
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
        gameManager = new GameManager(null); // Mocking Leaderboard for network tests
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

        //è un metodo che serve per aspettare in modo asincrono l'arrivo di uno specifico messaggio di rete durante un test, impostanto un tempo limite per evitare che il test si blocchi
        // è un approccio che si chiama "polling" : il thread del test controlla ripetutamente una coda (history) per vedere se un thread in background ha ricevuto il pacchetto atteso

        //<T extends ServerMessage>: Dichiara un tipo generico T (tipo di ritorno) che è vincolato a essere una classe che eredita da ServerMessage.
        //Class<T> clazz: È il parametro in cui passi la classe che stai aspettando (es. MatchmakingSuccessMessage.class). Passare il token della classe permette a Java di fare controlli sui tipi a runtime.
        public <T extends ServerMessage> T waitFor(Class<T> clazz, int timeoutSec) {
            long end = System.currentTimeMillis() + (timeoutSec * 1000L);
            while (System.currentTimeMillis() < end) {
                for (ServerMessage msg : history) {
                    if (clazz.isInstance(msg)) { //clazz.isInstance(msg): È l'equivalente a runtime dell'operatore instanceof
                        history.remove(msg);
                        return clazz.cast(msg); //clazz.cast(msg): Converte in modo sicuro l'oggetto generico ServerMessage nel tipo specifico T per restituirlo.
                    }
                }
                try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                //Se il messaggio non è nella history, il ciclo si ferma per 50 millisecondi prima di riprovare.
                // Questo evita di consumare il 100% della CPU in un loop a vuoto, dando al thread di rete il tempo materiale per ricevere e processare i pacchetti.
            }
            fail("Timeout: " + clazz.getSimpleName() + " not received for " + nickname);
            return null;
        }

        public void disconnect() { connection.disconnect(); }

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