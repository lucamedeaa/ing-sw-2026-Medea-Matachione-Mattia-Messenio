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
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        final Logger LOGGER = Logger.getLogger(ClientMain.class.getName());

        System.out.print(ColorAnsi.CLEAR);
        System.out.flush();
        printLogo();

        System.out.println(ColorAnsi.BLACK_BOLD + "━".repeat(52) + ColorAnsi.RESET);
        System.out.println("   " + ColorAnsi.WHITE_BOLD + "INITIAL JOURNEY SETTINGS" + ColorAnsi.RESET);
        System.out.println(ColorAnsi.BLACK_BOLD + "━".repeat(52) + ColorAnsi.RESET + "\n");

        int uiChoice = 0;
        while (uiChoice != 1 && uiChoice != 2) {
            System.out.println(" " + ColorAnsi.WHITE_BOLD + "How would you like to explore the world of Mesos?" + ColorAnsi.RESET);
            System.out.println(" " + ColorAnsi.YELLOW_BOLD + "[ 1 ]" + ColorAnsi.RESET + " " + ColorAnsi.ITALIC + "Parchment" + ColorAnsi.RESET + " (TUI)   " + ColorAnsi.YELLOW_BOLD + "[ 2 ]" + ColorAnsi.RESET + " " + ColorAnsi.ITALIC + "Magical Vision" + ColorAnsi.RESET + " (GUI)");
            System.out.print(" " + ColorAnsi.GREEN_BOLD + ">" + ColorAnsi.RESET + " ");
            try {
                uiChoice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println(" " + ColorAnsi.RED_BOLD + "✖ Invalid selection. Please enter 1 or 2." + ColorAnsi.RESET + "\n");
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
                System.out.println(" " + ColorAnsi.WHITE_BOLD + "Enter the server’s IP address:" + ColorAnsi.RESET);
                System.out.print(" " + ColorAnsi.GREEN_BOLD + ">" + ColorAnsi.RESET + " ");
                ip = scanner.nextLine().trim();
                if (ip.isEmpty() || !ip.matches("[a-zA-Z0-9.]+")) {
                    ip = "";
                    continue;
                }
            }

            // Scelta Rete
            if (networkChoice == 0) {
                System.out.println(" " + ColorAnsi.WHITE_BOLD + "Which network path would you like to follow?" + ColorAnsi.RESET);
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
                System.out.println(" " + ColorAnsi.WHITE_BOLD + "Which gate do you want to knock on? (port):" + ColorAnsi.RESET);
                System.out.print(" " + ColorAnsi.GREEN_BOLD + ">" + ColorAnsi.RESET + " ");
                try {
                    port = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    port = 0;
                    continue;
                }
            }

            System.out.println("\n " + ColorAnsi.CYAN_BOLD + "Connecting to the server..." + ColorAnsi.RESET);
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
                System.out.println("\n " + ColorAnsi.BG_RED_WHITE_TEXT + " RMI ERROR " + ColorAnsi.RESET + " No mention of the Mesos service was found on the ancient stones.");
                server = null;
                ip = "";
                networkChoice = 0;
                port = 0;
            } catch (java.io.IOException e) {
                // Errore previsto: Server spento, connessione rifiutata, timeout
                System.out.println("\n " + ColorAnsi.BG_RED_WHITE_TEXT + " DISTANT ECHO " + ColorAnsi.RESET + " Unable to contact the port: " + e.getMessage());
                System.out.println(" " + ColorAnsi.ITALIC + "Please try entering the details again." + ColorAnsi.RESET + "\n");
                server = null;
                ip = "";
                networkChoice = 0;
                port = 0;
            } catch (RuntimeException e) {
                // concetto di FAULT BARRIER: Cattura i crash interni di RMI o bug di programmazione.
                System.out.println("\n " + ColorAnsi.BG_RED_WHITE_TEXT + " CRITICAL ERROR OR INCORRECT PROTOCOL " + ColorAnsi.RESET);
                System.out.println(" An unexpected error has occurred (e.g. port with an incompatible protocol):");
                System.out.println(" Technical details: " + e.getClass().getName() + " - " + e.getMessage());
                System.out.print("\n Press ENTER to reset and try again > ");

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

            System.out.println(" " + ColorAnsi.GREEN_BOLD + "✔ Successfully connected!" + ColorAnsi.RESET + "\n");
            ui.start();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Critical error during UI startup", e);
        }
    }

    private static void printLogo() {
        ColorAnsi.printLogo(System.out::println);
    }
}