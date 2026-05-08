package it.polimi.ingsw.server.lobby;

import it.polimi.ingsw.server.network.RoomClientProxy;
import it.polimi.ingsw.server.model.exception.LobbyActionException;

public interface RoomConnectionHandler {

    RoomAdmissionResult addPlayer(String nickname, RoomClientProxy connection) throws LobbyActionException;

    void removePlayer(String nickname) throws LobbyActionException;
}
