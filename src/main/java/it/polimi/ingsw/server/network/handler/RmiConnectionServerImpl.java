package it.polimi.ingsw.server.network.handler;

import it.polimi.ingsw.server.controller.LobbyController;
import it.polimi.ingsw.common.rmi.RMIClientCallback;
import it.polimi.ingsw.common.rmi.RMIServerSession;
import it.polimi.ingsw.common.rmi.RMIConnectionServer;
import it.polimi.ingsw.server.lobby.GameManagerInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

public class RmiConnectionServerImpl extends UnicastRemoteObject implements RMIConnectionServer {
    private static final Logger LOGGER = Logger.getLogger(RmiConnectionServerImpl.class.getName());

    private final GameManagerInterface gameManager;
    private final LobbyController lobbyController;

    public RmiConnectionServerImpl(GameManagerInterface gameManager, LobbyController lobbyController) throws RemoteException {
        super();
        this.gameManager = gameManager;
        this.lobbyController = lobbyController;
    }

    @Override
    public RMIServerSession connect(RMIClientCallback clientCallback) throws RemoteException {
        LOGGER.info("[RMI] New connection request received. Creating dedicated handler...");

        RMmiClientHandler clientHandler = new RMmiClientHandler(gameManager, lobbyController, clientCallback);
        LOGGER.info("[RMI] Handler created and returned to client successfully.");
        return clientHandler;
    }
}
