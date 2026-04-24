package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.ServerMessage;
import it.polimi.ingsw.view.VirtualView;

public interface ClientConnection {

    void setVirtualView(VirtualView virtualView);

    void send(ServerMessage message);


}