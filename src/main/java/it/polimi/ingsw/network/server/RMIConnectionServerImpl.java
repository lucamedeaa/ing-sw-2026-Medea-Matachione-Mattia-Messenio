package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.network.rmi.RMIClientCallback;
import it.polimi.ingsw.network.rmi.RMIServerSession;
import it.polimi.ingsw.network.rmi.RMIConnectionServer;
import it.polimi.ingsw.server.GameManagerInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

public class RMIConnectionServerImpl extends UnicastRemoteObject implements RMIConnectionServer {
    private static final Logger LOGGER = Logger.getLogger(RMIConnectionServerImpl.class.getName());

    private final GameManagerInterface gameManager;
    private final LobbyController lobbyController;

    public RMIConnectionServerImpl(GameManagerInterface gameManager, LobbyController lobbyController) throws RemoteException {
        super();
        this.gameManager = gameManager;
        this.lobbyController = lobbyController;
    }

    @Override
    public RMIServerSession connect(RMIClientCallback clientCallback) throws RemoteException {
        LOGGER.info("[RMI] New connection request received. Creating dedicated handler...");

        RMIClientHandler clientHandler = new RMIClientHandler(gameManager, lobbyController, clientCallback);
        LOGGER.info("[RMI] Handler created and returned to client successfully.");
        return clientHandler;
    }
}
