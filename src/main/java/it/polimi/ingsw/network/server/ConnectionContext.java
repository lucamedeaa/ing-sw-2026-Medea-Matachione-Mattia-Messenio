package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameController;

public interface ConnectionContext extends RoomClientProxy {

    @Override
    void transitionToGameState(GameController gameController);

    void setNickname(String nickname);

    String getNickname();

    boolean isActive();

    @Override
    void returnToLobby(int playerCount);

}
