package it.polimi.ingsw.client.network;

import it.polimi.ingsw.network.client.ServerNotificationReceiver;
import it.polimi.ingsw.network.client.ServerProxy;

import java.io.IOException;
import java.rmi.NotBoundException;

public interface NetworkConnectionFactory {

    NetworkClientFactory.NetworkType type();

    ServerProxy create(String ip, int port, ServerNotificationReceiver receiver) throws IOException, NotBoundException;
}
