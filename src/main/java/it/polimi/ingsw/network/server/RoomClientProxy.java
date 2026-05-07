package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameController;

public interface RoomClientProxy extends ClientProxy {

    void transitionToGameState(GameController gameController);

    void returnToLobby(int playerCount);
}
