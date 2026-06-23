package it.polimi.ingsw.client.network.factory;

import it.polimi.ingsw.client.network.NetworkConnectionFactory;
import it.polimi.ingsw.client.network.RmiClientCallbackImpl;
import it.polimi.ingsw.client.network.RmiServerProxy;
import it.polimi.ingsw.client.network.ServerNotificationReceiver;
import it.polimi.ingsw.client.network.ServerProxy;
import it.polimi.ingsw.common.rmi.RMIConnectionServer;
import it.polimi.ingsw.common.rmi.RMIServerSession;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Client connection factory for the RMI transport.
 */
public class RMIConnectionFactory implements NetworkConnectionFactory {

    /** {@inheritDoc} */
    @Override
    public NetworkClientFactory.NetworkType type() {
        return NetworkClientFactory.NetworkType.RMI;
    }

    /** {@inheritDoc} */
    @Override
    public ServerProxy create(String ip, int port, ServerNotificationReceiver receiver) throws IOException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(ip, port);
        RMIConnectionServer server = (RMIConnectionServer) registry.lookup("MesosServer");
        RmiClientCallbackImpl callback = new RmiClientCallbackImpl(receiver);
        RMIServerSession session = server.connect(callback);
        return new RmiServerProxy(session, callback);
    }
}
