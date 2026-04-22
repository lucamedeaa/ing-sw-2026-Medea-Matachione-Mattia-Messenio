package it.polimi.ingsw.server;

import it.polimi.ingsw.network.messages.GameInfoDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {

    private final Map<String, GameRoom> activeGames = new ConcurrentHashMap<>();

    public synchronized String createNewGame(String creator, int maxPlayers) {
        String gameId = UUID.randomUUID().toString().substring(0, 8);
        GameRoom newRoom = new GameRoom(gameId, maxPlayers, this);
        activeGames.put(gameId, newRoom);
        return gameId;
    }

    public GameRoom getGame(String gameId) {
        return activeGames.get(gameId);
    }

    public synchronized List<GameInfoDTO> getAvailableGames() {
        List<GameInfoDTO> available = new ArrayList<>();
        for (GameRoom room : activeGames.values()) {
            if (!room.isFull() && !room.isGameStarted()) {
                available.add(new GameInfoDTO(room.getGameId(), room.getPlayers().get(0), room.getPlayers().size(), room.getMaxPlayers()));
            }
        }
        return available;
    }

    public synchronized void removeGame(String gameId) {
        activeGames.remove(gameId);
    }
}