package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.visitor.MatchmakingVisitor;
import it.polimi.ingsw.server.GameManager;
import it.polimi.ingsw.server.GameRoom;
import it.polimi.ingsw.server.exceptions.InvalidPlayerCountException;
import it.polimi.ingsw.server.exceptions.RoomFullException;

public class MatchmakingState implements MatchmakingVisitor {

    private final ClientConnection handler;
    private final GameManager gameManager;

    public MatchmakingState(ClientConnection handler, GameManager gameManager) {
        this.handler = handler;
        this.gameManager = gameManager;
    }

    @Override
    public void visit(CreateGameMessage msg) {
        createGame(msg.nickname(), msg.maxPlayers());
    }

    public void createGame(String nickname, int maxPlayers) {
        if (nickname == null || nickname.trim().isEmpty()) {
            handler.error("Invalid nickname: cannot be empty or null.");
            return;
        }
        if (!gameManager.registerNickname(nickname)) {
            handler.error("Nickname already in use on the server.");
            return;
        }
        try {
            String gameId = gameManager.createNewGame(nickname, maxPlayers);
            GameRoom room = gameManager.getGame(gameId);
            room.addPlayer(nickname, handler);
            handler.matchmakingSuccess("Game created. Waiting for other players...");
            room.broadcast("Il giocatore " + nickname + " è entrato nella stanza.");
        } catch (InvalidPlayerCountException | RoomFullException | IllegalStateException e) {
            gameManager.unregisterNickname(nickname);
            handler.error(e.getMessage());
        } catch (Exception e) {
            gameManager.unregisterNickname(nickname);
            e.printStackTrace();
            handler.error("Internal server error during game creation.");
        }
    }

    @Override
    public void visit(JoinGameMessage msg) {
        joinGame(msg.nickname(), msg.gameId());
    }

    public void joinGame(String nickname, String gameId) {
        GameRoom room = gameManager.getGame(gameId);
        if (nickname == null || nickname.trim().isEmpty()) {
            handler.error("Invalid nickname: cannot be empty or null.");
            return;
        }
        if (room == null) {
            handler.error("Requested game does not exist.");
            return;
        }
        if (!gameManager.registerNickname(nickname)) {
            handler.error("Nickname already in use on the server.");
            return;
        }
        try {
            room.addPlayer(nickname, handler);
            handler.matchmakingSuccess("Joined game successfully. Waiting to start...");
            if (!room.isGameStarted()) {
                room.broadcast("Il giocatore " + nickname + " è entrato.");
            }
        } catch (RoomFullException | IllegalStateException e) {
            gameManager.unregisterNickname(nickname);
            handler.error(e.getMessage());
        } catch (Exception e) {
            gameManager.unregisterNickname(nickname);
            e.printStackTrace();
            handler.error("Internal server error during join.");
        }
    }
    @Override
    public void visit(GetAvailableGamesMessage msg) {
        getAvailableGames();
    }

    public void getAvailableGames() {
        var availableGames = gameManager.getAvailableGames();
        handler.availableGames(availableGames);
    }

    @Override
    public void visit(LeaveGameMessage msg) {
        leaveGame();
    }

    public void leaveGame() {
        String playerName = handler.getNickname();
        if (playerName == null) {
            handler.error("Error: You don't have a nickname set.");
            return;
        }
        GameRoom room = gameManager.getGameRoomByPlayer(playerName);
        if (room == null) {
            handler.error("Error: You are not in any game room.");
            return;
        }
        try {
            room.removePlayer(playerName);
            handler.gameLeftSuccess("You left the lobby.");
        } catch (IllegalStateException e) {
            handler.error(e.getMessage());
        }
    }
}
