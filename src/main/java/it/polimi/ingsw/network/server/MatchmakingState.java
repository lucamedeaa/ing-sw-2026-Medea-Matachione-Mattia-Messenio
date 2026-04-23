package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.visitor.MatchmakingVisitor;
import it.polimi.ingsw.server.GameManager;
import it.polimi.ingsw.server.GameRoom;

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
        } catch (Exception e) {
            handler.send(new ErrorMessageDTO("Error during game creation: " + e.getMessage()));
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
        } catch (Exception e) {
            handler.send(new ErrorMessageDTO("Error during join: " + e.getMessage()));
        }
    }

    @Override
    public void visit(GetAvailableGamesMessage msg) {
        var availableGames = gameManager.getAvailableGames();
        handler.send(new AvailableGamesResponseMessage(availableGames));
    }
}