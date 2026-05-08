package it.polimi.ingsw.server;

import it.polimi.ingsw.network.messages.GameInfoDTO;
import it.polimi.ingsw.server.exceptions.LobbyActionException;
import it.polimi.ingsw.server.leaderboard.LeaderboardService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** Manages active game rooms, handling creation, lookup, listing, and removal of games. */
public class GameManager implements GameManagerInterface {

    private final Map<String, GameRoom> activeGames = new ConcurrentHashMap<>();
    private final Set<String> activeNicknames = ConcurrentHashMap.newKeySet();
    private final LeaderboardService leaderboardService;

    public GameManager(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    public boolean registerNickname(String nickname) {
        return activeNicknames.add(nickname);
    }

    public void unregisterNickname(String nickname) {
        if (nickname != null) {
            activeNicknames.remove(nickname);
        }
    }

    /** Creates a new game room and registers it. @param creator nickname of the creator @param maxPlayers maximum number of players @return generated game identifier */
    public String createNewGame(String creator, int maxPlayers) throws LobbyActionException {
        if (maxPlayers < 2 || maxPlayers > 5) {
            throw new LobbyActionException("Errore: inserire un numero di giocatori compreso tra 2 e 5.");
        }
        String gameId = UUID.randomUUID().toString().substring(0, 8);
        GameRoom newRoom = new GameRoom(gameId, maxPlayers, this, leaderboardService);
        activeGames.put(gameId, newRoom);
        return gameId;
    }

    /** Retrieves a room connection port by its identifier. @param gameId game identifier @return room connection port or null if not found */
    public RoomConnectionHandler getRoom(String gameId) {
        return activeGames.get(gameId);
    }

    public RoomConnectionHandler getRoomByPlayer(String nickname) {
        for (GameRoom room : activeGames.values()) {
            if (room.isNicknameTaken(nickname)) {
                return room;
            }
        }
        return null;
    }

    /** Returns the list of joinable games (not full and not started). @return list of available game info DTOs */
    public List<GameInfoDTO> getAvailableGames() {
        List<GameInfoDTO> available = new ArrayList<>();
        for (GameRoom room : activeGames.values()) {
            if (!room.isFull() && !room.isGameStarted()) {
                List<String> currentPlayers = room.getPlayers();
                String creatorName = currentPlayers.isEmpty() ? "Unknown" : currentPlayers.getFirst();

                available.add(new GameInfoDTO(
                        room.getGameId(),
                        creatorName,
                        currentPlayers.size(),
                        room.getMaxPlayers()
                ));
            }
        }
        return available;
    }

    /** Removes a game room from the active list. @param gameId game identifier */
    public void removeGame(String gameId) {
        activeGames.remove(gameId);
    }
}
