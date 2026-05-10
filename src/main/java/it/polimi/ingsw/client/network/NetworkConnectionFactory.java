package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.network.factory.NetworkClientFactory;

import java.io.IOException;
import java.rmi.NotBoundException;

/**
 * Factory for a specific client network transport.
 */
public interface NetworkConnectionFactory {

    /**
     * Returns the transport type created by this factory.
     *
     * @return supported network type
     */
    NetworkClientFactory.NetworkType type();

    /**
     * Opens a connection to the server.
     *
     * @param ip server host
     * @param port server port
     * @param receiver receiver for server notifications
     * @return proxy used by the client to send commands
     * @throws IOException if the transport cannot be opened
     * @throws NotBoundException if the RMI server name is not bound
     */
    ServerProxy create(String ip, int port, ServerNotificationReceiver receiver) throws IOException, NotBoundException;
}
