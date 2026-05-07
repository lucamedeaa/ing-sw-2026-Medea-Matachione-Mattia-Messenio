package it.polimi.ingsw.client.network;

import it.polimi.ingsw.network.client.RMIClientCallbackImpl;
import it.polimi.ingsw.network.client.RMIServerProxy;
import it.polimi.ingsw.network.client.ServerNotificationReceiver;
import it.polimi.ingsw.network.client.ServerProxy;
import it.polimi.ingsw.network.rmi.RMIConnectionServer;
import it.polimi.ingsw.network.rmi.RMIServerSession;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIConnectionFactory implements NetworkConnectionFactory {

    @Override
    public NetworkClientFactory.NetworkType type() {
        return NetworkClientFactory.NetworkType.RMI;
    }

    @Override
    public ServerProxy create(String ip, int port, ServerNotificationReceiver receiver) throws Exception {
        Registry registry = LocateRegistry.getRegistry(ip, port);
        RMIConnectionServer server = (RMIConnectionServer) registry.lookup("MesosServer");
        RMIClientCallbackImpl callback = new RMIClientCallbackImpl(receiver);
        RMIServerSession session = server.connect(callback);
        return new RMIServerProxy(session, callback);
    }
}
