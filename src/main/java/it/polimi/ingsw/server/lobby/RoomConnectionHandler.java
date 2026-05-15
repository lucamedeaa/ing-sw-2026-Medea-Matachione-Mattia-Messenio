package it.polimi.ingsw.server.lobby;

import it.polimi.ingsw.server.network.ConnectionContext;
import it.polimi.ingsw.server.model.exception.LobbyActionException;

/**
 * Handles player membership operations for a room.
 */
public interface RoomConnectionHandler {

    /**
     * Adds a player session to the room.
     *
     * @param nickname nickname reserved for the player
     * @param connection connection session associated with the player
     * @return deferred room effects to run after matchmaking confirmation
     * @throws LobbyActionException if the player cannot be admitted
     */
    RoomAdmissionResult addPlayer(String nickname, ConnectionContext connection) throws LobbyActionException;

    /**
     * Removes a player from the room, or forwards the disconnection to the game controller after start.
     *
     * @param nickname nickname of the player to remove
     */
    void removePlayer(String nickname);
}
