package it.polimi.ingsw.server;

import it.polimi.ingsw.network.messages.GameInfoDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Manages active game rooms, handling creation, lookup, listing, and removal of games. */
public class GameManager {

    private final Map<String, GameRoom> activeGames = new ConcurrentHashMap<>();

    /** Creates a new game room and registers it. @param creator nickname of the creator @param maxPlayers maximum number of players @return generated game identifier */
    public synchronized String createNewGame(String creator, int maxPlayers) {
        String gameId = UUID.randomUUID().toString().substring(0, 8);
        GameRoom newRoom = new GameRoom(gameId, maxPlayers, this);
        activeGames.put(gameId, newRoom);
        return gameId;
    }

    /** Retrieves a game room by its identifier. @param gameId game identifier @return corresponding GameRoom or null if not found */
    public GameRoom getGame(String gameId) {
        return activeGames.get(gameId);
    }


    public GameRoom getGameRoomByPlayer(String nickname) {
        for (GameRoom room : activeGames.values()) {
            if (room.isNicknameTaken(nickname)) {
                return room;
            }
        }
        return null;
    }

    /** Returns the list of joinable games (not full and not started). @return list of available game info DTOs */
    public synchronized List<GameInfoDTO> getAvailableGames() {
        List<GameInfoDTO> available = new ArrayList<>();
        for (GameRoom room : activeGames.values()) {
            if (!room.isFull() && !room.isGameStarted()) {
                available.add(new GameInfoDTO(
                        room.getGameId(),
                        room.getPlayers().get(0),
                        room.getPlayers().size(),
                        room.getMaxPlayers()
                ));
            }
        }
        return available;
    }

    /** Removes a game room from the active list. @param gameId game identifier */
    public synchronized void removeGame(String gameId) {
        activeGames.remove(gameId);
    }
}