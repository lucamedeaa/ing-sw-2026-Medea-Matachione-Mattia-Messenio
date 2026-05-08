package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.network.factory.NetworkClientFactory;

import java.io.IOException;
import java.rmi.NotBoundException;

public interface NetworkConnectionFactory {

    NetworkClientFactory.NetworkType type();

    ServerProxy create(String ip, int port, ServerNotificationReceiver receiver) throws IOException, NotBoundException;
}
