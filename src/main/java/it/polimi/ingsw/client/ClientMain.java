package it.polimi.ingsw.client;

import it.polimi.ingsw.client.model.LightGameModel;
import it.polimi.ingsw.client.network.ClientMessageReceiver;
import it.polimi.ingsw.client.network.NetworkClientFactory;
import it.polimi.ingsw.network.visitor.ClientMessageVisitor;
import it.polimi.ingsw.network.client.VirtualServer;
import it.polimi.ingsw.network.messages.CreateGameMessage;
import it.polimi.ingsw.network.messages.JoinGameMessage;

import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) {
        //TODO: Move the Scanner/CLI logic to a proper View class later.
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== BENVENUTO IN MESOS ===");
        System.out.print("Inserisci l'IP del Server (es. 127.0.0.1 o localhost): ");
        String ip = scanner.nextLine();

        System.out.println("\nScegli la tecnologia di rete:");
        System.out.println("1. Socket TCP");
        System.out.println("2. RMI");
        System.out.print("Scelta: ");
        int networkTypeInt = Integer.parseInt(scanner.nextLine());
        NetworkClientFactory.NetworkType networkType = (networkTypeInt == 1) ? 
                NetworkClientFactory.NetworkType.SOCKET : NetworkClientFactory.NetworkType.RMI;

        System.out.print("\nInserisci il tuo Nickname: ");
        String nickname = scanner.nextLine();

        LightGameModel lightModel = new LightGameModel();
        ClientMessageVisitor messageReceiver = new ClientMessageReceiver(lightModel);

        try {
            System.out.println("\nVuoi creare una nuova partita o unirti a una esistente?");
            System.out.println("1. Crea nuova partita");
            System.out.println("2. Unisciti a una partita");
            System.out.print("Scelta: ");
            int action = Integer.parseInt(scanner.nextLine());

            int maxPlayers = 0;
            String gameId = "";

            if (action == 1) {
                System.out.print("\nNumero massimo di giocatori (2-5): ");
                maxPlayers = Integer.parseInt(scanner.nextLine());
            } else {
                System.out.print("\nInserisci l'ID della partita: ");
                gameId = scanner.nextLine();
            }

            int port = (networkType == NetworkClientFactory.NetworkType.SOCKET) ? 1234 : 1099;

            VirtualServer connection = NetworkClientFactory.createConnection(
                    networkType,
                    ip,
                    port,
                    messageReceiver,
                    nickname,
                    action,
                    maxPlayers,
                    gameId
            );

            // If using Socket, we need to send the matchmaking message over the created connection.
            // RMI does this during session creation.
            if (networkType == NetworkClientFactory.NetworkType.SOCKET) {
                if (action == 1) {
                    connection.sendMessage(new CreateGameMessage(nickname, maxPlayers));
                } else {
                    connection.sendMessage(new JoinGameMessage(nickname, gameId));
                }
            }

            System.out.println("\n[SETUP COMPLETATO] In attesa dei dati dal server...");

        } catch (Exception e) {
            System.err.println("\n[ERRORE FATALE] Impossibile connettersi al server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}