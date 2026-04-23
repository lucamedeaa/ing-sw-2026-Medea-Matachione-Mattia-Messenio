package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.rmi.RMIClientCallback;
import it.polimi.ingsw.network.rmi.RMIServerSession;
import it.polimi.ingsw.network.server.ClientConnection;
import it.polimi.ingsw.network.rmi.RMIConnectionServer;
import it.polimi.ingsw.server.GameManager;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIConnectionServerImpl extends UnicastRemoteObject implements RMIConnectionServer {
    private final GameManager gameManager;
    public RMIConnectionServerImpl(GameManager gameManager) throws RemoteException {
        super();
        this.gameManager = gameManager;
    }

    @Override
    public RMIServerSession connect(RMIClientCallback clientCallback) throws RemoteException {
        System.out.println("[RMI] Nuova richiesta di connessione ricevuta. Generazione handler dedicato...");

        try {
                RMIClientHandler clientHandler = new RMIClientHandler(gameManager, clientCallback);
            System.out.println("[RMI] Handler generato e restituito al client con successo.");
            return clientHandler;

        } catch (RemoteException e) {
            System.err.println("[RMI] Errore durante la creazione dell'handler per il client: " + e.getMessage());
            throw e;
        }
    }
}