package it.polimi.ingsw.client;
import it.polimi.ingsw.client.lightGameModel.LightGameModel;
import it.polimi.ingsw.client.network.ClientMessageReceiver;
import it.polimi.ingsw.client.network.NetworkClientFactory;
import it.polimi.ingsw.client.network.ServerController;
import it.polimi.ingsw.client.view.ClientUI;
import it.polimi.ingsw.client.view.UIFactory;
import it.polimi.ingsw.network.client.VirtualServer;

import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int uiChoice = 0;
        while (uiChoice != 1 && uiChoice != 2) {
            System.out.print("\033[H\033[2J");
            System.out.println("Scegli: [1] TUI  [2] GUI");
            try { uiChoice = Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Invalid choice."); }
        }

        String ip = "";
        while (ip.isEmpty() || !ip.matches("[a-zA-Z0-9.]+")) {
            System.out.println("IP del server:");
            ip = scanner.nextLine().trim();
            if (ip.isEmpty() || !ip.matches("[a-zA-Z0-9.]+"))
                System.out.println("Invalid IP.");
        }

        int networkChoice = 0;
        while (networkChoice != 1 && networkChoice != 2) {
            System.out.println("Scegli: [1] Socket  [2] RMI");
            try { networkChoice = Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Invalid choice."); }
        }

        NetworkClientFactory.NetworkType type = networkChoice == 1
                ? NetworkClientFactory.NetworkType.SOCKET
                : NetworkClientFactory.NetworkType.RMI;

        int port = 0;
        while (port <= 0 || port > 65535) {
            System.out.println("Porta:");
            try { port = Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Invalid port."); }
        }

        LightGameModel model = new LightGameModel();

        try {
            ClientUI ui = UIFactory.create(uiChoice, model);
            ClientMessageReceiver receiver = new ClientMessageReceiver(model, ui);
            VirtualServer server = NetworkClientFactory.createConnection(type, ip, port, receiver);
            ServerController controller = new ServerController(server);
            ui.setController(controller);
            ui.start(); // blocca qui — loop scanner nel thread principale
        } catch (Exception e) {
            System.err.println("Errore di connessione: " + e.getMessage());
        }
    }
}
