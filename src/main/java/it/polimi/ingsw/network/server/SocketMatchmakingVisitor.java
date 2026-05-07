package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.CreateGameMessage;
import it.polimi.ingsw.network.messages.GetAvailableGamesMessage;
import it.polimi.ingsw.network.messages.JoinGameMessage;
import it.polimi.ingsw.network.visitor.MatchmakingVisitor;

public class SocketMatchmakingVisitor implements MatchmakingVisitor {

    private final ConnectionState connectionState;

    public SocketMatchmakingVisitor(ConnectionState connectionState) {
        this.connectionState = connectionState;
    }

    @Override
    public void visit(CreateGameMessage msg) {
        connectionState.createGame(msg.nickname(), msg.maxPlayers());
    }

    @Override
    public void visit(JoinGameMessage msg) {
        connectionState.joinGame(msg.nickname(), msg.gameId());
    }

    @Override
    public void visit(GetAvailableGamesMessage msg) {
        connectionState.getAvailableGames();
    }
}
