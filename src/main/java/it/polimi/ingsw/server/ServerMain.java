package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.LobbyController;
import it.polimi.ingsw.server.lobby.GameManager;
import it.polimi.ingsw.server.network.handler.SocketClientHandler;
import it.polimi.ingsw.server.network.handler.RmiConnectionServerImpl;
import it.polimi.ingsw.server.leaderboard.JdbcLeaderboardService;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Main server class that starts both RMI and Socket services and manages incoming client connections. */
public class ServerMain {

    private static final Logger LOGGER = Logger.getLogger(ServerMain.class.getName());
    private static final String LEADERBOARD_DB_URL = "jdbc:postgresql://localhost:5432/mesos";
    private static final String LEADERBOARD_DB_USER = "mesos";
    private static final String LEADERBOARD_DB_PASSWORD = "mesos";

    private final int socketPort;
    private final int rmiPort;
    private final GameManager gameManager;
    private final LobbyController lobbyController;

    /** Constructs the server with given ports. @param socketPort port for socket connections @param rmiPort port for RMI registry */
    public ServerMain(int socketPort, int rmiPort) {
        this.socketPort = socketPort;
        this.rmiPort = rmiPort;
        this.gameManager = new GameManager(new JdbcLeaderboardService(
                LEADERBOARD_DB_URL,
                LEADERBOARD_DB_USER,
                LEADERBOARD_DB_PASSWORD
        ));
        this.lobbyController = new LobbyController(gameManager);
    }

    /** Starts the server by initializing both RMI and Socket services. */
    public void start() {
        LOGGER.info("Starting Mesos Server");
        startRMIServer();
        startSocketServer();
    }

    /** Initializes and binds the RMI matchmaking service. */
    private void startRMIServer() {
        try {
            String myIp = resolveRmiHostname();
            System.setProperty("java.rmi.server.hostname", myIp);
            LOGGER.info("[RMI] Automatic hostname configuration: " + myIp);

            RmiConnectionServerImpl entryPoint = new RmiConnectionServerImpl(gameManager, lobbyController);
            Registry registry = LocateRegistry.createRegistry(rmiPort);
            registry.rebind("MesosServer", entryPoint);
            LOGGER.info("[RMI] Listening for connections on port " + rmiPort);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "[RMI] Fatal error during startup", e);
        }
    }

    private String resolveRmiHostname() throws IOException {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        } catch (IOException e) {
            LOGGER.log(Level.FINE, "[RMI] Could not detect route address; falling back to localhost", e);
            return InetAddress.getLocalHost().getHostAddress();
        }
    }

    /** Starts the socket server and listens for incoming client connections. */
    private void startSocketServer() {
        try (ServerSocket serverSocket = new ServerSocket(socketPort)) {
            LOGGER.info("[SOCKET] Listening for connections on port " + socketPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                LOGGER.info("[SOCKET] New connection from: " + clientSocket.getInetAddress());

                SocketClientHandler clientHandler = new SocketClientHandler(clientSocket, gameManager, lobbyController);
                new Thread(clientHandler).start();
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "[SOCKET] Fatal server error", e);
        }
    }

    /** Entry point of the server application with default ports. @param args command-line arguments */
    public static void main(String[] args) {
        int defaultSocketPort = 1234;
        int defaultRmiPort = 1099;
        ServerMain server = new ServerMain(defaultSocketPort, defaultRmiPort);
        server.start();
    }
}
