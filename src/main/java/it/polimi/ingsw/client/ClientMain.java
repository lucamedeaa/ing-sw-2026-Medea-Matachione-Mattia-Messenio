package it.polimi.ingsw.client;

import it.polimi.ingsw.client.lightGameModel.LightGameModel;
import it.polimi.ingsw.client.network.ClientMessageReceiver;
import it.polimi.ingsw.client.network.NetworkClientFactory;
import it.polimi.ingsw.client.network.ServerController;
import it.polimi.ingsw.client.view.ClientUI;
import it.polimi.ingsw.client.view.UIFactory;
import it.polimi.ingsw.network.client.ServerProxy;

import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\033[H\033[2J");
        System.out.flush();
        printLogo();

        System.out.println("\033[1;30m" + "━".repeat(52) + "\033[0m");
        System.out.println("   \033[1;37mCONFIGURAZIONE INIZIALE DEL VIAGGIO\033[0m");
        System.out.println("\033[1;30m" + "━".repeat(52) + "\033[0m\n");

        int uiChoice = 0;
        while (uiChoice != 1 && uiChoice != 2) {
            System.out.println(" \033[1;37mCome desideri visualizzare il mondo di Mesos?\033[0m");
            System.out.println(" \033[1;33m[ 1 ]\033[0m \033[3mPergamena\033[0m (TUI)   \033[1;33m[ 2 ]\033[0m \033[3mVisione Magica\033[0m (GUI)");
            System.out.print(" \033[1;32m>\033[0m ");
            try { uiChoice = Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println(" \033[1;31m✖ Scelta non valida. Inserisci 1 o 2.\033[0m\n"); }
        }
        System.out.println();

        String ip = "";
        while (ip.isEmpty() || !ip.matches("[a-zA-Z0-9.]+")) {
            System.out.println(" \033[1;37mInserisci le coordinate del Server (IP):");
            System.out.print(" \033[1;32m>\033[0m ");
            ip = scanner.nextLine().trim();
            if (ip.isEmpty() || !ip.matches("[a-zA-Z0-9.]+")) {
                System.out.println(" \033[1;31m✖ Formato IP non valido.\033[0m\n");
            }
        }
        System.out.println();

        int networkChoice = 0;
        while (networkChoice != 1 && networkChoice != 2) {
            System.out.println(" \033[1;37mQuale sentiero di rete vuoi percorrere?\033[0m");
            System.out.println(" \033[1;33m[ 1 ]\033[0m \033[3mSocket [TCP]\033[0m          \033[1;33m[ 2 ]\033[0m \033[3mRMI\033[0m");
            System.out.print(" \033[1;32m>\033[0m ");
            try { networkChoice = Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println(" \033[1;31m✖ Scelta non valida. Inserisci 1 o 2.\033[0m\n"); }
        }
        NetworkClientFactory.NetworkType type = networkChoice == 1
                ? NetworkClientFactory.NetworkType.SOCKET
                : NetworkClientFactory.NetworkType.RMI;
        System.out.println();

        int port = 0;
        while (port <= 0 || port > 65535) {
            System.out.println(" \033[1;37mA quale varco vuoi bussare? (Porta):");
            System.out.print(" \033[1;32m>\033[0m ");
            try { port = Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println(" \033[1;31m✖ Porta non valida. Inserisci un numero tra 1 e 65535.\033[0m\n"); }
        }
        System.out.println("\n \033[1;36mConnessione al server in corso...\033[0m");

        LightGameModel model = new LightGameModel();
        try {
            ClientUI ui = UIFactory.create(uiChoice, model);
            ClientMessageReceiver receiver = new ClientMessageReceiver(model, ui);
            ServerProxy server = NetworkClientFactory.createConnection(type, ip, port, receiver);
            ServerController controller = new ServerController(server);
            ui.setController(controller);

            ui.start();
        } catch (Exception e) {
            System.err.println("\n \033[1;41;37m ERRORE FATALE \033[0m \033[1;31mImpossibile connettersi: " + e.getMessage() + "\033[0m");
        }
    }

    private static void printLogo() {
        String[] logoLines = {
                "███╗   ███╗███████╗███████╗ ██████╗ ███████╗",
                "████╗ ████║██╔════╝██╔════╝██╔═══██╗██╔════╝",
                "██╔████╔██║█████╗  ███████╗██║   ██║███████╗",
                "██║╚██╔╝██║██╔══╝  ╚════██║██║   ██║╚════██║",
                "██║ ╚═╝ ██║███████╗███████║╚██████╔╝███████║",
                "╚═╝     ╚═╝╚══════╝╚══════╝ ╚═════╝ ╚══════╝"
        };
        String[] colors = {"\033[38;5;226m", "\033[38;5;220m", "\033[38;5;214m", "\033[38;5;208m", "\033[38;5;202m", "\033[38;5;166m"};

        for (int i = 0; i < logoLines.length; i++) {
            System.out.println(colors[i] + logoLines[i] + "\033[0m");
        }
    }
}
