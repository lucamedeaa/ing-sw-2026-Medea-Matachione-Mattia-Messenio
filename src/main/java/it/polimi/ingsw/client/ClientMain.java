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
        System.out.println("IP del server:");
        String ip = scanner.nextLine();
        int networkChoice = 0;
        while (networkChoice != 1 && networkChoice != 2) {
            System.out.println("Scegli: [1] Socket  [2] RMI");
            try { networkChoice = Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Invalid choice."); }
        }
        NetworkClientFactory.NetworkType type = networkChoice == 1
                ? NetworkClientFactory.NetworkType.SOCKET
                : NetworkClientFactory.NetworkType.RMI;
        int port = -1;
        while (port < 0 || port > 65535) {
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
        /* l'idea di base è che questo coso deve chiedere solo se vuoi mettere GUI o TUI. Poi si
        apre la GUI/TUI e scegli se usare socket o RMI. A quel punto usando networkClientFactory, vi darà
        un virtualServer. Questo è un virtualServer generico, voi vi dovete preoccupare soltanto di usare il metodo send d'ora in
        poi del tipo di messaggio che volete inviare sia per socket che per rmi su quel virtual server.
        Per questa fase inziale, ho creato i messaggi GetAvailableGameMessage, che vi da la lista di games disponibili
        JoinGameMessage che dandogli l' id e un nickname vi aggiunge al game e CreateGameMessage, che crea un game con numero di giocatori che gli dite voi.
        Per ogni errore in questa fase mando un semplice ErrorMessageDTO, che contiene una stringa con la spiegazione del motivo
        . Non penso che serva molto di più che visualizzare una stringa, sia nella GUI che nella TUI,
        per questo ne ho fatto uno unico per tutti e non ho fatto classi per ogni errore.
        In generale penso che per i messaggi di Errore basti fare un DTO così e scriverci il messagio, anche dopo ma ditemi cosa ne pensate (in modo da fare un unico visit).
        Se ho un successo dopo creazione o entrata ricevo un MatchMakingSuccessMessage, che significa che o ho creato la partita e sono entrato (anche qua una stringa mi dirà quale
        delle due, è una semplice stringa) o AvailableGamesResponseMessage, che semplicemente mi dice gli id disponibili.
         Ora ho scritto dei visit stupidi nel ClientMessageReceiver, ma ovviamente
        bisogna trovare un modo di delegare alla view (deve essere un modo coerente con le scelte fatte fino ad ora)
        il visit di questi. Una volta fatto questo inizia il game, quindi si può inizare a usare la vostra logica con il lightgamemodel.
        DAJE RAGA!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        TODO miei: aggiungere possibilità di uscire da una lobby e relativi messaggi. Gestire disconnessioni anche involontarie in fase di lobby, probabilmente facendo un ping ogni tot
        TODO (capisci come farlo bene). Mettere un timer per le mosse del player quando è il proprio turno. Creare eventuali eccezioni custom + messaggi di errore per eccezioni
        TODO nel controller. Riguardare e sistemare codice
         */