package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.visitor.MatchmakingVisitor;
import it.polimi.ingsw.server.GameManager;
import it.polimi.ingsw.server.GameRoom;
import it.polimi.ingsw.server.exceptions.InvalidPlayerCountException;
import it.polimi.ingsw.server.exceptions.NicknameTakenException;
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
        try {
            String gameId = gameManager.createNewGame(msg.nickname(), msg.maxPlayers());
            GameRoom room = gameManager.getGame(gameId);
            room.addPlayer(msg.nickname(), handler);
            handler.send(new MatchmakingSuccessMessage("Game created. Waiting for other players..."));
        } catch (InvalidPlayerCountException | RoomFullException | NicknameTakenException | IllegalStateException e) {
            handler.send(new ErrorMessageDTO(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            handler.send(new ErrorMessageDTO("Internal server error during game creation."));
        }
    }

    @Override
    public void visit(JoinGameMessage msg) {
        try {
            GameRoom room = gameManager.getGame(msg.gameId());
            if (room == null) {
                handler.send(new ErrorMessageDTO("Requested game does not exist."));
                return;
            }
            room.addPlayer(msg.nickname(), handler);
            handler.send(new MatchmakingSuccessMessage("Joined game successfully. Waiting to start..."));
        } catch (RoomFullException | NicknameTakenException | IllegalStateException e) {
            handler.send(new ErrorMessageDTO(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            handler.send(new ErrorMessageDTO("Internal server error during join."));
        }
    }
    @Override
    public void visit(GetAvailableGamesMessage msg) {
        var availableGames = gameManager.getAvailableGames();
        handler.send(new AvailableGamesResponseMessage(availableGames));
    }

    @Override
    public void visit(LeaveGameMessage msg) {
        String playerName = handler.getNickname();
        if (playerName == null) {
            handler.send(new ErrorMessageDTO("Error: You don't have a nickname set."));
            return;
        }
        GameRoom room = gameManager.getGameRoomByPlayer(playerName);
        if (room == null) {
            handler.send(new ErrorMessageDTO("Error: You are not in any game room."));
            return;
        }
        try {
            room.removePlayer(playerName);
            handler.send(new GameLeftSuccessMessage("You left the lobby."));
        } catch (IllegalStateException e) {
            handler.send(new ErrorMessageDTO(e.getMessage()));
        }
    }
}