package it.polimi.ingsw.client.network;

import it.polimi.ingsw.network.client.ServerNotificationReceiver;
import it.polimi.ingsw.network.client.ServerProxy;
import it.polimi.ingsw.network.client.SocketServerConnection;
import it.polimi.ingsw.network.client.SocketServerProxy;

public class SocketConnectionFactory implements NetworkConnectionFactory {

    @Override
    public NetworkClientFactory.NetworkType type() {
        return NetworkClientFactory.NetworkType.SOCKET;
    }

    @Override
    public ServerProxy create(String ip, int port, ServerNotificationReceiver receiver) throws Exception {
        SocketServerMessageVisitor socketVisitor = new SocketServerMessageVisitor(receiver);
        SocketServerConnection socketConn = new SocketServerConnection(ip, port, socketVisitor);
        new Thread(socketConn).start();
        return new SocketServerProxy(socketConn);
    }
}
