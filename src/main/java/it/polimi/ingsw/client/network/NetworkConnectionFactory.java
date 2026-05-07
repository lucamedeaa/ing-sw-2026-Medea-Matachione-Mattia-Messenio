package it.polimi.ingsw.client.network;

import it.polimi.ingsw.network.client.ServerNotificationReceiver;
import it.polimi.ingsw.network.client.ServerProxy;

public interface NetworkConnectionFactory {

    NetworkClientFactory.NetworkType type();

    ServerProxy create(String ip, int port, ServerNotificationReceiver receiver) throws Exception;
}
