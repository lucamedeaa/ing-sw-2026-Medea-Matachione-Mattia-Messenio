package it.polimi.ingsw.client.network.factory;

import it.polimi.ingsw.client.network.NetworkConnectionFactory;
import it.polimi.ingsw.client.network.ServerNotificationReceiver;
import it.polimi.ingsw.client.network.ServerProxy;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Selects the proper client connection factory for the requested network type.
 */
public class NetworkClientFactory {

    private final Map<NetworkType, NetworkConnectionFactory> factories;

    /**
     * Creates a selector from the available transport factories.
     *
     * @param factories supported connection factories
     */
    public NetworkClientFactory(List<NetworkConnectionFactory> factories) {
        this.factories = factories.stream()
                .collect(Collectors.toMap(NetworkConnectionFactory::type, factory -> factory));
    }

    /**
     * Supported client transport types.
     */
    public enum NetworkType {
        SOCKET,
        RMI
    }

    /**
     * Opens a server connection using the selected transport.
     *
     * @param type transport type
     * @param ip server host
     * @param port server port
     * @param receiver receiver for server notifications
     * @return server proxy for sending commands
     * @throws IOException if the selected transport cannot connect
     * @throws NotBoundException if the RMI binding is missing
     */
    public ServerProxy createConnection(
            NetworkType type,
            String ip,
            int port,
            ServerNotificationReceiver receiver) throws IOException, NotBoundException {
        if (type == null) {
            throw new IllegalArgumentException("Network type cannot be null.");
        }
        NetworkConnectionFactory factory = factories.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("Unsupported network type: " + type);
        }
        return factory.create(ip, port, receiver);
    }
}
