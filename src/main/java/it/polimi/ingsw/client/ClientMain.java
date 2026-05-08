package it.polimi.ingsw.client;

import it.polimi.ingsw.client.lightGameModel.EventApplier;
import it.polimi.ingsw.client.lightGameModel.LobbyModel;
import it.polimi.ingsw.client.lightGameModel.MatchModel;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.network.NetworkClientFactory;
import it.polimi.ingsw.client.network.RMIConnectionFactory;
import it.polimi.ingsw.client.network.ServerController;
import it.polimi.ingsw.client.network.SocketConnectionFactory;
import it.polimi.ingsw.client.tui.render.AnsiColors;
import it.polimi.ingsw.client.view.ClientUI;
import it.polimi.ingsw.client.view.UIFactory;
import it.polimi.ingsw.network.client.ServerProxy;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.Scanner;
import java.util.List;

public class ClientMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print(AnsiColors.CLEAR);
        System.out.flush();
        printLogo();

        System.out.println(AnsiColors.BLACK_BOLD + "━".repeat(52) + AnsiColors.RESET);
        System.out.println("   " + AnsiColors.WHITE_BOLD + "CONFIGURAZIONE INIZIALE DEL VIAGGIO" + AnsiColors.RESET);
        System.out.println(AnsiColors.BLACK_BOLD + "━".repeat(52) + AnsiColors.RESET + "\n");

        int uiChoice = 0;
        while (uiChoice != 1 && uiChoice != 2) {
            System.out.println(" " + AnsiColors.WHITE_BOLD + "Come desideri visualizzare il mondo di Mesos?" + AnsiColors.RESET);
            System.out.println(" " + AnsiColors.YELLOW_BOLD + "[ 1 ]" + AnsiColors.RESET + " " + AnsiColors.ITALIC + "Pergamena" + AnsiColors.RESET + " (TUI)   " + AnsiColors.YELLOW_BOLD + "[ 2 ]" + AnsiColors.RESET + " " + AnsiColors.ITALIC + "Visione Magica" + AnsiColors.RESET + " (GUI)");
            System.out.print(" " + AnsiColors.GREEN_BOLD + ">" + AnsiColors.RESET + " ");
            try {
                uiChoice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println(" " + AnsiColors.RED_BOLD + "✖ Scelta non valida. Inserisci 1 o 2." + AnsiColors.RESET + "\n");
            }
        }
        System.out.println();

        LobbyModel lobbyModel = new LobbyModel();
        MatchModel matchModel = new MatchModel();
        EventApplier eventApplier = new EventApplier(matchModel);
        ClientNotificationController receiver = new ClientNotificationController(lobbyModel, matchModel, eventApplier);

        // Prepariamo la UI e passiamo il receiver
        ClientUI ui = UIFactory.create(uiChoice, lobbyModel, matchModel, scanner);
        ui.setNotificationController(receiver);

        ServerProxy server = null;
        String ip = "";
        int networkChoice = 0;
        int port = 0;

        while (server == null) {
            // Richiesta IP
            if (ip.isEmpty()) {
                System.out.println(" " + AnsiColors.WHITE_BOLD + "Inserisci le coordinate del Server (IP):" + AnsiColors.RESET);
                System.out.print(" " + AnsiColors.GREEN_BOLD + ">" + AnsiColors.RESET + " ");
                ip = scanner.nextLine().trim();
                if (ip.isEmpty() || !ip.matches("[a-zA-Z0-9.]+")) {
                    ip = "";
                    continue;
                }
            }

            // Scelta Rete
            if (networkChoice == 0) {
                System.out.println(" " + AnsiColors.WHITE_BOLD + "Quale sentiero di rete vuoi percorrere?" + AnsiColors.RESET);
                System.out.println(" " + AnsiColors.YELLOW_BOLD + "[ 1 ]" + AnsiColors.RESET + " " + AnsiColors.ITALIC + "Socket [TCP]" + AnsiColors.RESET + "          " + AnsiColors.YELLOW_BOLD + "[ 2 ]" + AnsiColors.RESET + " " + AnsiColors.ITALIC + "RMI" + AnsiColors.RESET);
                System.out.print(" " + AnsiColors.GREEN_BOLD + ">" + AnsiColors.RESET + " ");
                try {
                    networkChoice = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    networkChoice = 0;
                    continue;
                }
            }

            // Richiesta Porta
            if (port <= 0 || port > 65535) {
                System.out.println(" " + AnsiColors.WHITE_BOLD + "A quale varco vuoi bussare? (Porta):" + AnsiColors.RESET);
                System.out.print(" " + AnsiColors.GREEN_BOLD + ">" + AnsiColors.RESET + " ");
                try {
                    port = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    port = 0;
                    continue;
                }
            }

            System.out.println("\n " + AnsiColors.CYAN_BOLD + "Connessione al server in corso..." + AnsiColors.RESET);
            try {
                NetworkClientFactory.NetworkType type = (networkChoice == 1)
                        ? NetworkClientFactory.NetworkType.SOCKET
                        : NetworkClientFactory.NetworkType.RMI;

                NetworkClientFactory networkFactory = new NetworkClientFactory(List.of(
                        new SocketConnectionFactory(),
                        new RMIConnectionFactory()
                ));

                // UNICA CONNESSIONE: passiamo il receiver reale direttamente
                server = networkFactory.createConnection(type, ip, port, receiver);

            } catch (java.rmi.NotBoundException e) {
                // Errore previsto: Il server c'è ma il servizio "MesosServer" non è registrato
                System.out.println("\n " + AnsiColors.BG_RED_WHITE_TEXT + " ERRORE RMI " + AnsiColors.RESET + " Il servizio Mesos non è stato trovato sulle pietre antiche.");
                server = null;
                ip = "";
                networkChoice = 0;
                port = 0;
            } catch (java.io.IOException e) {
                // Errore previsto: Server spento, connessione rifiutata, timeout
                System.out.println("\n " + AnsiColors.BG_RED_WHITE_TEXT + " ECO DISTANTE " + AnsiColors.RESET + " Impossibile contattare la porta: " + e.getMessage());
                System.out.println(" " + AnsiColors.ITALIC + "Riprova a inserire i dati." + AnsiColors.RESET + "\n");
                server = null;
                ip = "";
                networkChoice = 0;
                port = 0;
            } catch (RuntimeException e) {
                // concetto di FAULT BARRIER: Cattura i crash interni di RMI o bug di programmazione.
                System.out.println("\n " + AnsiColors.BG_RED_WHITE_TEXT + " ANOMALIA CRITICA O PROTOCOLLO ERRATO " + AnsiColors.RESET);
                System.out.println(" Si è verificato un errore inaspettato (es. porta con protocollo incompatibile):");
                System.out.println(" Dettaglio tecnico: " + e.getClass().getName() + " - " + e.getMessage());
                System.out.print("\n Premi INVIO per ripristinare e riprovare > ");

                scanner.nextLine();

                server = null;
                ip = "";
                networkChoice = 0;
                port = 0;
            }
        }

        try {
            ServerController controller = new ServerController(server);
            ui.setController(controller);

            System.out.println(" " + AnsiColors.GREEN_BOLD + "✔ Connesso con successo!" + AnsiColors.RESET + "\n");
            ui.start();
        } catch (Exception e) {
            System.err.println("Errore critico durante l'avvio della UI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printLogo() {
        AnsiColors.printLogo(System.out::println);
    }
}