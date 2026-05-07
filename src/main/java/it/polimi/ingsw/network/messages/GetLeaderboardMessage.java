package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.visitor.AfterGameVisitor;

public record GetLeaderboardMessage() implements AfterGameMessage {
    @Override
    public void accept(AfterGameVisitor visitor) {
        visitor.visit(this);
    }
}
