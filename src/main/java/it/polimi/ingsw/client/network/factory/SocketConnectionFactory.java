package it.polimi.ingsw.client.network.factory;

import it.polimi.ingsw.client.network.NetworkConnectionFactory;
import it.polimi.ingsw.client.network.SocketServerMessageVisitor;
import it.polimi.ingsw.client.network.ServerNotificationReceiver;
import it.polimi.ingsw.client.network.ServerProxy;
import it.polimi.ingsw.client.network.SocketServerConnection;
import it.polimi.ingsw.client.network.SocketServerProxy;

import java.io.IOException;

/**
 * Client connection factory for the socket transport.
 */
public class SocketConnectionFactory implements NetworkConnectionFactory {

    /** {@inheritDoc} */
    @Override
    public NetworkClientFactory.NetworkType type() {
        return NetworkClientFactory.NetworkType.SOCKET;
    }

    /** {@inheritDoc} */
    @Override
    public ServerProxy create(String ip, int port, ServerNotificationReceiver receiver) throws IOException {
        SocketServerMessageVisitor socketVisitor = new SocketServerMessageVisitor(receiver);
        SocketServerConnection socketConn = new SocketServerConnection(ip, port, socketVisitor);
        new Thread(socketConn).start();
        return new SocketServerProxy(socketConn);
    }
}
