package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.messages.ClientMessage;

public interface VirtualServer{

    void sendMessage(ClientMessage message);
    void disconnect();
}

