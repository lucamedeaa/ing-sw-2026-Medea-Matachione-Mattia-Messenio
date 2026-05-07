package it.polimi.ingsw.server;

import it.polimi.ingsw.network.server.SocketClientHandler;
import it.polimi.ingsw.network.server.RMIConnectionServerImpl;
import it.polimi.ingsw.server.leaderboard.InMemoryLeaderboardService;

import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.IOException;

/** Main server class that starts both RMI and Socket services and manages incoming client connections. */
public class GameServer {

    private final int socketPort;
    private final int rmiPort;
    private final GameManager gameManager;

    /** Constructs the server with given ports. @param socketPort port for socket connections @param rmiPort port for RMI registry */
    public GameServer(int socketPort, int rmiPort) {
        this.socketPort = socketPort;
        this.rmiPort = rmiPort;
        this.gameManager = new GameManager(new InMemoryLeaderboardService());
    }

    /** Starts the server by initializing both RMI and Socket services. */
    public void start() {
        System.out.println("=== Starting Mesos Server ===");
        startRMIServer();
        startSocketServer();
    }

    /** Initializes and binds the RMI matchmaking service. */
    private void startRMIServer() {
        try {
            //SUS
            String myIp;
            try (java.net.DatagramSocket socket = new java.net.DatagramSocket()) {
                // Finta connessione UDP per forzare l'OS a esporre l'IP della rotta principale
                socket.connect(java.net.InetAddress.getByName("8.8.8.8"), 10002);
                myIp = socket.getLocalAddress().getHostAddress();
            } catch (Exception e) {
                // Fallback in caso di assenza totale di connessione
                myIp = java.net.InetAddress.getLocalHost().getHostAddress();
            }
            System.setProperty("java.rmi.server.hostname", myIp);
            System.out.println("[RMI] Configurazione hostname automatica: " + myIp);
            //SUS

            RMIConnectionServerImpl entryPoint = new RMIConnectionServerImpl(gameManager);
            Registry registry = LocateRegistry.createRegistry(rmiPort);
            registry.rebind("MesosServer", entryPoint);
            System.out.println("[RMI] Listening for connections on port " + rmiPort);
        } catch (Exception e) {
            System.err.println("[RMI] Fatal error during startup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Starts the socket server and listens for incoming client connections. */
    private void startSocketServer() {
        try (ServerSocket serverSocket = new ServerSocket(socketPort)) {
            System.out.println("[SOCKET] Listening for connections on port " + socketPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SOCKET] New connection from: " + clientSocket.getInetAddress());

                try {
                    SocketClientHandler clientHandler = new SocketClientHandler(clientSocket, gameManager);
                    new Thread(clientHandler).start();
                } catch (IOException e) {
                    System.err.println("[SOCKET] Errore di I/O durante l'inizializzazione del client: " + e.getMessage());
                    try {
                        clientSocket.close();
                    } catch (IOException ignored) {}
                }
            }

        } catch (Exception e) {
            System.err.println("[SOCKET] Fatal server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Entry point of the server application with default ports. @param args command-line arguments */
    public static void main(String[] args) {
        int defaultSocketPort = 1234;
        int defaultRmiPort = 1099;
        GameServer server = new GameServer(defaultSocketPort, defaultRmiPort);
        server.start();
    }
}