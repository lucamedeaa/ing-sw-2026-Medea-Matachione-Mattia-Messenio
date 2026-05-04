package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.ServerMessage;
import it.polimi.ingsw.virtualView.VirtualView;

public interface ClientConnection {

    void setVirtualView(VirtualView virtualView);

    void send(ServerMessage message);

    void setNickname(String nickname);

    String getNickname();

    void returnToLobby(int playerCount);

}