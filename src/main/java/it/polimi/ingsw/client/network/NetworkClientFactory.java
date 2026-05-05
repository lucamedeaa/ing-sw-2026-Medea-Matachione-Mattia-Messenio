package it.polimi.ingsw.client.network;

import it.polimi.ingsw.network.client.*;
import it.polimi.ingsw.network.rmi.RMIConnectionServer;
import it.polimi.ingsw.network.rmi.RMIServerSession;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class NetworkClientFactory {

    public enum NetworkType {
        SOCKET,
        RMI
    }

    public static ServerProxy createConnection(
            NetworkType type,
            String ip,
            int port,
            ClientNetworkReceiver receiver) throws Exception {
        if (type == NetworkType.SOCKET) {
            SocketServerConnection socketConn = new SocketServerConnection(ip, port, receiver);
            new Thread(socketConn).start();
            return new SocketServerProxy(socketConn);
        } else {
            Registry registry = LocateRegistry.getRegistry(ip, port);
            RMIConnectionServer server = (RMIConnectionServer) registry.lookup("MesosServer");
            RMIClientCallbackImpl callback = new RMIClientCallbackImpl(receiver);
            RMIServerSession session = server.connect(callback);
            return new RMIServerProxy(session, callback);
        }
    }
}
