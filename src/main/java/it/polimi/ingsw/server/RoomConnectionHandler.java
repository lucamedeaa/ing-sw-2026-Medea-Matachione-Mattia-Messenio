package it.polimi.ingsw.server;

import it.polimi.ingsw.network.server.RoomClientProxy;
import it.polimi.ingsw.server.exceptions.RoomFullException;

public interface RoomConnectionHandler {

    RoomAdmissionResult addPlayer(String nickname, RoomClientProxy connection) throws RoomFullException;

    void removePlayer(String nickname) throws IllegalStateException;
}
