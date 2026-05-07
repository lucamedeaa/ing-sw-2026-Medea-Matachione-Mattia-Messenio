package it.polimi.ingsw.server;

import it.polimi.ingsw.network.messages.GameInfoDTO;
import it.polimi.ingsw.server.exceptions.InvalidPlayerCountException;

import java.util.List;

public interface GameManagerInterface {

    boolean registerNickname(String nickname);

    void unregisterNickname(String nickname);

    String createNewGame(String creatorNickname, int maxPlayers) throws InvalidPlayerCountException;

    RoomConnectionHandler getRoom(String gameId);

    List<GameInfoDTO> getAvailableGames();

    RoomConnectionHandler getRoomByPlayer(String nickname);

    void removeGame(String gameId);
}
