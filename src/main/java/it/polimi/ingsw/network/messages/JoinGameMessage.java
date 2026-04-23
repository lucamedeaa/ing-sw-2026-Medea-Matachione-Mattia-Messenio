package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.visitor.MatchmakingVisitor;

public record JoinGameMessage(String nickname, String gameId) implements MatchmakingMessage {
    @Override
    public void accept(MatchmakingVisitor visitor) {
        visitor.visit(this);
    }
}