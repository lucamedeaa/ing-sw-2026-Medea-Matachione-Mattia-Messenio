package it.polimi.ingsw.server.network.handler;

import it.polimi.ingsw.server.controller.LobbyController;
import it.polimi.ingsw.common.rmi.RMIClientCallback;
import it.polimi.ingsw.common.rmi.RMIServerSession;
import it.polimi.ingsw.common.rmi.RMIConnectionServer;
import it.polimi.ingsw.server.lobby.GameManagerInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

/**
 * RMI entry point that creates a dedicated server session for each connecting client.
 */
public class RmiConnectionServerImpl extends UnicastRemoteObject implements RMIConnectionServer {
    private static final Logger LOGGER = Logger.getLogger(RmiConnectionServerImpl.class.getName());

    private final GameManagerInterface gameManager;
    private final LobbyController lobbyController;

    /**
     * Creates the shared RMI connection server.
     *
     * @param gameManager game manager shared by client handlers
     * @param lobbyController lobby controller shared by client handlers
     * @throws RemoteException if the remote object cannot be exported
     */
    public RmiConnectionServerImpl(GameManagerInterface gameManager, LobbyController lobbyController) throws RemoteException {
        super();
        this.gameManager = gameManager;
        this.lobbyController = lobbyController;
    }

    /**
     * Connects a new RMI client and returns its dedicated session.
     *
     * @param clientCallback callback exported by the client
     * @return dedicated RMI server session
     * @throws RemoteException if the session cannot be created or returned
     */
    @Override
    public RMIServerSession connect(RMIClientCallback clientCallback) throws RemoteException {
        LOGGER.info("[RMI] New connection request received. Creating dedicated handler...");

        RmiClientHandler clientHandler = new RmiClientHandler(gameManager, lobbyController, clientCallback);
        LOGGER.info("[RMI] Handler created and returned to client successfully.");
        return clientHandler;
    }
}
