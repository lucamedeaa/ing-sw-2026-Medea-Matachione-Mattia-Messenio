package it.polimi.ingsw.server.lobby;

import it.polimi.ingsw.common.network.dto.GameInfoDto;
import it.polimi.ingsw.server.model.exception.LobbyActionException;

import java.util.List;

/** Defines the contract for game manager interface. */
public interface GameManagerInterface {

    boolean registerNickname(String nickname);

    void unregisterNickname(String nickname);

    String createNewGame(String creatorNickname, int maxPlayers) throws LobbyActionException;

    RoomConnectionHandler getRoom(String gameId);

    List<GameInfoDto> getAvailableGames();

    RoomConnectionHandler getRoomByPlayer(String nickname);

    void removeGame(String gameId);
}
