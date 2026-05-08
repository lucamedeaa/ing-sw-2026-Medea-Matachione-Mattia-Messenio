package it.polimi.ingsw.client;

import it.polimi.ingsw.client.controller.ServerController;
import it.polimi.ingsw.client.model.EventApplier;
import it.polimi.ingsw.client.model.LobbyModel;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.network.factory.NetworkClientFactory;
import it.polimi.ingsw.client.network.factory.RMIConnectionFactory;
import it.polimi.ingsw.client.network.factory.SocketConnectionFactory;
import it.polimi.ingsw.client.view.tui.render.ColorAnsi;
import it.polimi.ingsw.client.view.ClientUi;
import it.polimi.ingsw.client.view.UiFactory;
import it.polimi.ingsw.client.network.ServerProxy;

import java.util.Scanner;
import java.util.List;

public class ClientMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print(ColorAnsi.CLEAR);
        System.out.flush();
        printLogo();

        System.out.println(ColorAnsi.BLACK_BOLD + "━".repeat(52) + ColorAnsi.RESET);
        System.out.println("   " + ColorAnsi.WHITE_BOLD + "CONFIGURAZIONE INIZIALE DEL VIAGGIO" + ColorAnsi.RESET);
        System.out.println(ColorAnsi.BLACK_BOLD + "━".repeat(52) + ColorAnsi.RESET + "\n");

        int uiChoice = 0;
        while (uiChoice != 1 && uiChoice != 2) {
            System.out.println(" " + ColorAnsi.WHITE_BOLD + "Come desideri visualizzare il mondo di Mesos?" + ColorAnsi.RESET);
            System.out.println(" " + ColorAnsi.YELLOW_BOLD + "[ 1 ]" + ColorAnsi.RESET + " " + ColorAnsi.ITALIC + "Pergamena" + ColorAnsi.RESET + " (TUI)   " + ColorAnsi.YELLOW_BOLD + "[ 2 ]" + ColorAnsi.RESET + " " + ColorAnsi.ITALIC + "Visione Magica" + ColorAnsi.RESET + " (GUI)");
            System.out.print(" " + ColorAnsi.GREEN_BOLD + ">" + ColorAnsi.RESET + " ");
            try {
                uiChoice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println(" " + ColorAnsi.RED_BOLD + "✖ Scelta non valida. Inserisci 1 o 2." + ColorAnsi.RESET + "\n");
            }
        }
        System.out.println();

        LobbyModel lobbyModel = new LobbyModel();
        GameModel gameModel = new GameModel();
        EventApplier eventApplier = new EventApplier(gameModel);
        ClientNotificationController receiver = new ClientNotificationController(lobbyModel, gameModel, eventApplier);

        // Prepariamo la UI e passiamo il receiver
        ClientUi ui = UiFactory.create(uiChoice, lobbyModel, gameModel, scanner);
        ui.setNotificationController(receiver);

        ServerProxy server = null;
        String ip = "";
        int networkChoice = 0;
        int port = 0;

        while (server == null) {
            // Richiesta IP
            if (ip.isEmpty()) {
                System.out.println(" " + ColorAnsi.WHITE_BOLD + "Inserisci le coordinate del Server (IP):" + ColorAnsi.RESET);
                System.out.print(" " + ColorAnsi.GREEN_BOLD + ">" + ColorAnsi.RESET + " ");
                ip = scanner.nextLine().trim();
                if (ip.isEmpty() || !ip.matches("[a-zA-Z0-9.]+")) {
                    ip = "";
                    continue;
                }
            }

            // Scelta Rete
            if (networkChoice == 0) {
                System.out.println(" " + ColorAnsi.WHITE_BOLD + "Quale sentiero di rete vuoi percorrere?" + ColorAnsi.RESET);
                System.out.println(" " + ColorAnsi.YELLOW_BOLD + "[ 1 ]" + ColorAnsi.RESET + " " + ColorAnsi.ITALIC + "Socket [TCP]" + ColorAnsi.RESET + "          " + ColorAnsi.YELLOW_BOLD + "[ 2 ]" + ColorAnsi.RESET + " " + ColorAnsi.ITALIC + "RMI" + ColorAnsi.RESET);
                System.out.print(" " + ColorAnsi.GREEN_BOLD + ">" + ColorAnsi.RESET + " ");
                try {
                    networkChoice = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    networkChoice = 0;
                    continue;
                }
            }

            // Richiesta Porta
            if (port <= 0 || port > 65535) {
                System.out.println(" " + ColorAnsi.WHITE_BOLD + "A quale varco vuoi bussare? (Porta):" + ColorAnsi.RESET);
                System.out.print(" " + ColorAnsi.GREEN_BOLD + ">" + ColorAnsi.RESET + " ");
                try {
                    port = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    port = 0;
                    continue;
                }
            }

            System.out.println("\n " + ColorAnsi.CYAN_BOLD + "Connessione al server in corso..." + ColorAnsi.RESET);
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
                System.out.println("\n " + ColorAnsi.BG_RED_WHITE_TEXT + " ERRORE RMI " + ColorAnsi.RESET + " Il servizio Mesos non è stato trovato sulle pietre antiche.");
                server = null;
                ip = "";
                networkChoice = 0;
                port = 0;
            } catch (java.io.IOException e) {
                // Errore previsto: Server spento, connessione rifiutata, timeout
                System.out.println("\n " + ColorAnsi.BG_RED_WHITE_TEXT + " ECO DISTANTE " + ColorAnsi.RESET + " Impossibile contattare la porta: " + e.getMessage());
                System.out.println(" " + ColorAnsi.ITALIC + "Riprova a inserire i dati." + ColorAnsi.RESET + "\n");
                server = null;
                ip = "";
                networkChoice = 0;
                port = 0;
            } catch (RuntimeException e) {
                // concetto di FAULT BARRIER: Cattura i crash interni di RMI o bug di programmazione.
                System.out.println("\n " + ColorAnsi.BG_RED_WHITE_TEXT + " ANOMALIA CRITICA O PROTOCOLLO ERRATO " + ColorAnsi.RESET);
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

            System.out.println(" " + ColorAnsi.GREEN_BOLD + "✔ Connesso con successo!" + ColorAnsi.RESET + "\n");
            ui.start();
        } catch (Exception e) {
            System.err.println("Errore critico durante l'avvio della UI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printLogo() {
        ColorAnsi.printLogo(System.out::println);
    }
}