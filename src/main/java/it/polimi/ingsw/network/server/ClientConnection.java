package it.polimi.ingsw.network.server;

import it.polimi.ingsw.virtualView.VirtualView;

public interface ClientConnection extends ClientProxy {

    void setVirtualView(VirtualView virtualView);

    void setNickname(String nickname);

    String getNickname();

    void returnToLobby(int playerCount);

}
