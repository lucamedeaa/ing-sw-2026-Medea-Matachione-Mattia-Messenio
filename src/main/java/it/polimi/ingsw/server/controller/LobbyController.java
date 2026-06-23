package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.common.network.dto.GameInfoDto;
import it.polimi.ingsw.server.lobby.GameManagerInterface;
import it.polimi.ingsw.server.lobby.RoomConnectionHandler;
import it.polimi.ingsw.server.model.exception.LobbyActionException;
import java.util.List;

/** Coordinates lobby requests against the active game manager. */
public class LobbyController {

    private final GameManagerInterface gameManager;

    /**
     * Creates a new {@code LobbyController} instance.
     *
     * @param gameManager game manager
     */
    public LobbyController(GameManagerInterface gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Creates a game room for a nickname.
     *
     * @param nickname player nickname
     * @param maxPlayers maximum number of players
     * @return handler for the created room
     * @throws LobbyActionException if the operation cannot be completed
     */
    public RoomConnectionHandler createGame(String nickname, int maxPlayers) throws LobbyActionException {
        validateNickname(nickname);
        registerNickname(nickname);
        boolean committed = false;
        try {
            String gameId = gameManager.createNewGame(nickname, maxPlayers);
            RoomConnectionHandler room = gameManager.getRoom(gameId);
            if (room == null) {
                throw new IllegalStateException("Created game does not exist.");
            }
            committed = true;
            return room;
        } finally {
            if (!committed) {
                gameManager.unregisterNickname(nickname);
            }
        }
    }

    /**
     * Resolves a joinable game room for a nickname.
     *
     * @param nickname player nickname
     * @param gameId game identifier
     * @return handler for the requested room
     * @throws LobbyActionException if the operation cannot be completed
     */
    public RoomConnectionHandler joinGame(String nickname, String gameId) throws LobbyActionException {
        validateNickname(nickname);
        RoomConnectionHandler room = gameManager.getRoom(gameId);
        if (room == null) {
            throw new LobbyActionException("Requested game does not exist.");
        }
        registerNickname(nickname);
        return room;
    }

    /**
     * Returns the available games.
     *
     * @return the currently joinable games
     */
    public List<GameInfoDto> getAvailableGames() {
        return gameManager.getAvailableGames();
    }

    /**
     * Leaves the game.
     *
     * @param playerName player name
     * @throws LobbyActionException if the operation cannot be completed
     */
    public void leaveGame(String playerName) throws LobbyActionException {
        if (playerName == null) {
            throw new LobbyActionException("Error: You don't have a nickname set.");
        }
        RoomConnectionHandler room = gameManager.getRoomByPlayer(playerName);
        if (room == null) {
            throw new LobbyActionException("Error: You are not in any game room.");
        }
        room.removePlayer(playerName);
    }

    /**
     * Handles the disconnection.
     *
     * @param playerName player name
     */
    public void handleDisconnection(String playerName) {
        if (playerName == null) {
            return;
        }
        RoomConnectionHandler room = gameManager.getRoomByPlayer(playerName);
        if (room != null) {
            room.removePlayer(playerName);
        } else {
            gameManager.unregisterNickname(playerName);
        }
    }

    /**
     * Releases the nickname.
     *
     * @param nickname player nickname
     */
    public void releaseNickname(String nickname) {
        if (nickname != null) {
            gameManager.unregisterNickname(nickname);
        }
    }

    private void validateNickname(String nickname) throws LobbyActionException {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new LobbyActionException("Invalid nickname: cannot be empty or null.");
        }
    }

    private void registerNickname(String nickname) throws LobbyActionException {
        if (!gameManager.registerNickname(nickname)) {
            throw new LobbyActionException("Nickname already in use on the server.");
        }
    }
}
