package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.visitor.MatchmakingVisitor;

public record LeaveGameMessage() implements MatchmakingMessage {
    @Override
    public void accept(MatchmakingVisitor visitor) {
        visitor.visit(this);
    }
}