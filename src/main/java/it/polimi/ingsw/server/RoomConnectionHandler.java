package it.polimi.ingsw.server;

import it.polimi.ingsw.network.server.RoomClientProxy;
import it.polimi.ingsw.server.exceptions.LobbyActionException;

public interface RoomConnectionHandler {

    RoomAdmissionResult addPlayer(String nickname, RoomClientProxy connection) throws LobbyActionException;

    void removePlayer(String nickname) throws LobbyActionException;
}
