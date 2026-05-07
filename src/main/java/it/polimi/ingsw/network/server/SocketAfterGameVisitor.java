package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.GetLeaderboardMessage;
import it.polimi.ingsw.network.visitor.AfterGameVisitor;

public class SocketAfterGameVisitor implements AfterGameVisitor {
    private final ConnectionState connectionState;

    public SocketAfterGameVisitor(ConnectionState connectionState) {
        this.connectionState = connectionState;
    }

    @Override
    public void visit(GetLeaderboardMessage msg) {
        connectionState.getLeaderboard();
    }
}
