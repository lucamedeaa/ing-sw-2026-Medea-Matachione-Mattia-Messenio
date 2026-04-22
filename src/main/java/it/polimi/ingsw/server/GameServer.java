package it.polimi.ingsw.server;

import it.polimi.ingsw.network.server.RMIMatchmakingServiceImpl;
import it.polimi.ingsw.network.server.SocketClientHandler;

import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class GameServer {

    private final int socketPort;
    private final int rmiPort;
    private final GameManager gameManager;

    public GameServer(int socketPort, int rmiPort) {
        this.socketPort = socketPort;
        this.rmiPort = rmiPort;
        this.gameManager = new GameManager();
    }

    public void start() {
        System.out.println("=== Avvio Server di Mesos ===");
        startRMIServer();
        startSocketServer();
    }


    private void startRMIServer() {
        //TODO: gestire try catch bene
        try {
            RMIMatchmakingServiceImpl matchmakingService = new RMIMatchmakingServiceImpl(gameManager);
            Registry registry = LocateRegistry.createRegistry(rmiPort);
            registry.rebind("MesosMatchmaking", matchmakingService);
            System.out.println("[RMI] Servizio di Matchmaking avviato sulla porta " + rmiPort);
            System.out.println("[RMI] Nome del servizio esposto: 'MesosMatchmaking'");

        } catch (Exception e) {
            System.err.println("[RMI] ERRORE FATALE durante l'avvio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void startSocketServer() {
        try (ServerSocket serverSocket = new ServerSocket(socketPort)) {
            System.out.println("[SOCKET] In ascolto per nuove connessioni sulla porta " + socketPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SOCKET] Nuova connessione rilevata da: " + clientSocket.getInetAddress());
                SocketClientHandler clientHandler = new SocketClientHandler(clientSocket, gameManager);
                new Thread(clientHandler).start();
            }

        } catch (Exception e) {
            System.err.println("[SOCKET] ERRORE FATALE del server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // TODO: selezione input
        int defaultSocketPort = 1234;
        int defaultRmiPort = 1099;
        GameServer server = new GameServer(defaultSocketPort, defaultRmiPort);
        server.start();
    }
}