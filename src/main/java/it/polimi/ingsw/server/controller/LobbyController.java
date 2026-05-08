package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.common.network.dto.GameInfoDto;
import it.polimi.ingsw.server.lobby.GameManagerInterface;
import it.polimi.ingsw.server.lobby.RoomConnectionHandler;
import it.polimi.ingsw.server.model.exception.LobbyActionException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LobbyController {

    private static final Logger LOGGER = Logger.getLogger(LobbyController.class.getName());

    private final GameManagerInterface gameManager;

    public LobbyController(GameManagerInterface gameManager) {
        this.gameManager = gameManager;
    }

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

    public RoomConnectionHandler joinGame(String nickname, String gameId) throws LobbyActionException {
        validateNickname(nickname);
        RoomConnectionHandler room = gameManager.getRoom(gameId);
        if (room == null) {
            throw new LobbyActionException("Requested game does not exist.");
        }
        registerNickname(nickname);
        return room;
    }

    public List<GameInfoDto> getAvailableGames() {
        return gameManager.getAvailableGames();
    }

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

    public void handleDisconnection(String playerName) {
        if (playerName == null) {
            return;
        }
        RoomConnectionHandler room = gameManager.getRoomByPlayer(playerName);
        if (room != null) {
            try {
                room.removePlayer(playerName);
            } catch (LobbyActionException e) {
                LOGGER.log(Level.FINE, "[LOBBY] Late lobby disconnection for: " + playerName, e);
            }
        } else {
            gameManager.unregisterNickname(playerName);
        }
    }

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
