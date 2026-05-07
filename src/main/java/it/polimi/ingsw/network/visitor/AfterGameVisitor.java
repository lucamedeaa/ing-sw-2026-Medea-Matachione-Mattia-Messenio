package it.polimi.ingsw.network.visitor;

import it.polimi.ingsw.network.messages.GetLeaderboardMessage;

public interface AfterGameVisitor {
    void visit(GetLeaderboardMessage msg);
}
