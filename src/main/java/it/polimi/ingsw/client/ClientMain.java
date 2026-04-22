package it.polimi.ingsw.client;

import it.polimi.ingsw.network.messages.ClientMessageVisitor;
import it.polimi.ingsw.network.client.VirtualServer;
import it.polimi.ingsw.network.client.SocketServerConnection;
import it.polimi.ingsw.network.client.RMIServerConnection;
import it.polimi.ingsw.network.client.RMIClientCallbackImpl;
import it.polimi.ingsw.network.messages.CreateGameMessage;
import it.polimi.ingsw.network.messages.JoinGameMessage;
import it.polimi.ingsw.network.rmi.RMIClientCallback;
import it.polimi.ingsw.network.rmi.RMIMatchmakingService;
import it.polimi.ingsw.network.rmi.RMIServerSession;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) {
        //TODO: sto coso fa schifo e va riscritto di baee
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== BENVENUTO IN MESOS ===");
        System.out.print("Inserisci l'IP del Server (es. 127.0.0.1 o localhost): ");
        String ip = scanner.nextLine();

        System.out.println("\nScegli la tecnologia di rete:");
        System.out.println("1. Socket TCP");
        System.out.println("2. RMI");
        System.out.print("Scelta: ");
        int networkType = Integer.parseInt(scanner.nextLine());

        System.out.print("\nInserisci il tuo Nickname: ");
        String nickname = scanner.nextLine();

        //TODO: scrivere con scelta GUI o CLI
        ClientMessageVisitor view = null;

        try {
            VirtualServer connection;

            System.out.println("\nVuoi creare una nuova partita o unirti a una esistente?");
            System.out.println("1. Crea nuova partita");
            System.out.println("2. Unisciti a una partita");
            System.out.print("Scelta: ");
            int action = Integer.parseInt(scanner.nextLine());

            if (networkType == 1) {
                int socketPort = 1234;
                SocketServerConnection socketConn = new SocketServerConnection(ip, socketPort, view);
                new Thread(socketConn).start();
                connection = socketConn;

                if (action == 1) {
                    System.out.print("\nNumero massimo di giocatori (2-5): ");
                    int maxPlayers = Integer.parseInt(scanner.nextLine());
                    connection.sendMessage(new CreateGameMessage(nickname, maxPlayers));
                } else {
                    System.out.print("\nInserisci l'ID della partita: ");
                    String gameId = scanner.nextLine();
                    connection.sendMessage(new JoinGameMessage(nickname, gameId));
                }

            } else {
                int rmiPort = 1099;
                Registry registry = LocateRegistry.getRegistry(ip, rmiPort);
                RMIMatchmakingService lobby = (RMIMatchmakingService) registry.lookup("MesosMatchmaking");

                RMIClientCallback callback = new RMIClientCallbackImpl(view);
                RMIServerSession session;
                //TODO: visitor in socket nella fase iniziale?

                if (action == 1) {
                    System.out.print("\nNumero massimo di giocatori (2-5): ");
                    int maxPlayers = Integer.parseInt(scanner.nextLine());
                    session = lobby.createGame(nickname, maxPlayers, callback);
                } else {
                    System.out.print("\nInserisci limport it.polimi.ingsw.controller.GameController;'ID della partita: ");
                    String gameId = scanner.nextLine();
                    session = lobby.joinGame(gameId, nickname, callback);
                }

                connection = new RMIServerConnection(session);
            }
            //TODO: gestire race conditions, settare conncetion (virtualServer) alla view, farlo in un ordine giusto ziopera
            //gestione timer per vedere disconnessione

            System.out.println("\n[SETUP COMPLETATO] In attesa dei dati dal server...");

        } catch (Exception e) {
            System.err.println("\n[ERRORE FATALE] Impossibile connettersi al server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
