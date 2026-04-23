package it.polimi.ingsw.network.visitor;

import it.polimi.ingsw.network.messages.CreateGameMessage;
import it.polimi.ingsw.network.messages.JoinGameMessage;
import it.polimi.ingsw.network.messages.GetAvailableGamesMessage;

public interface MatchmakingVisitor {
    void visit(CreateGameMessage msg);
    void visit(JoinGameMessage msg);
    void visit(GetAvailableGamesMessage msg);
}