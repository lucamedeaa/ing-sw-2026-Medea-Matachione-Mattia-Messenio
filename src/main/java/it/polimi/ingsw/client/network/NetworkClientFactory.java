package it.polimi.ingsw.client.network;

import it.polimi.ingsw.network.client.ServerNotificationReceiver;
import it.polimi.ingsw.network.client.ServerProxy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NetworkClientFactory {

    private final Map<NetworkType, NetworkConnectionFactory> factories;

    public NetworkClientFactory(List<NetworkConnectionFactory> factories) {
        this.factories = factories.stream()
                .collect(Collectors.toMap(NetworkConnectionFactory::type, factory -> factory));
    }

    public enum NetworkType {
        SOCKET,
        RMI
    }

    public ServerProxy createConnection(
            NetworkType type,
            String ip,
            int port,
            ServerNotificationReceiver receiver) throws Exception {
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
