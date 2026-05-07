package it.polimi.ingsw.controller;

import it.polimi.ingsw.network.messages.GameInfoDTO;
import it.polimi.ingsw.server.GameManagerInterface;
import it.polimi.ingsw.server.RoomConnectionHandler;
import it.polimi.ingsw.server.exceptions.InvalidPlayerCountException;
import java.util.List;

public class LobbyController {

    private final GameManagerInterface gameManager;

    public LobbyController(GameManagerInterface gameManager) {
        this.gameManager = gameManager;
    }

    public RoomConnectionHandler createGame(String nickname, int maxPlayers) throws InvalidPlayerCountException {
        validateNickname(nickname);
        registerNickname(nickname);
        try {
            String gameId = gameManager.createNewGame(nickname, maxPlayers);
            RoomConnectionHandler room = gameManager.getRoom(gameId);
            if (room == null) {
                throw new IllegalStateException("Created game does not exist.");
            }
            return room;
        } catch (InvalidPlayerCountException e) {
            gameManager.unregisterNickname(nickname);
            throw e;
        } catch (RuntimeException e) {
            gameManager.unregisterNickname(nickname);
            throw e;
        }
    }

    public RoomConnectionHandler joinGame(String nickname, String gameId) {
        validateNickname(nickname);
        RoomConnectionHandler room = gameManager.getRoom(gameId);
        if (room == null) {
            throw new IllegalArgumentException("Requested game does not exist.");
        }
        registerNickname(nickname);
        return room;
    }

    public List<GameInfoDTO> getAvailableGames() {
        return gameManager.getAvailableGames();
    }

    public void leaveGame(String playerName) {
        if (playerName == null) {
            throw new IllegalStateException("Error: You don't have a nickname set.");
        }
        RoomConnectionHandler room = gameManager.getRoomByPlayer(playerName);
        if (room == null) {
            throw new IllegalStateException("Error: You are not in any game room.");
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
            } catch (IllegalStateException e) {
                System.out.println("[LOBBY] Disconnessione tardiva in lobby per: " + playerName);
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

    private void validateNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid nickname: cannot be empty or null.");
        }
    }

    private void registerNickname(String nickname) {
        if (!gameManager.registerNickname(nickname)) {
            throw new IllegalStateException("Nickname already in use on the server.");
        }
    }
}
