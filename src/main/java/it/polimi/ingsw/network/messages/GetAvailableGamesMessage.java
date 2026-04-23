package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.visitor.MatchmakingVisitor;

public record GetAvailableGamesMessage() implements MatchmakingMessage {
    @Override
    public void accept(MatchmakingVisitor visitor) {
        visitor.visit(this);
    }
}