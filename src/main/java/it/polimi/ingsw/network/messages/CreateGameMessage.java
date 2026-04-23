package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.visitor.MatchmakingVisitor;

public record CreateGameMessage(String nickname, int maxPlayers) implements MatchmakingMessage {
    @Override
    public void accept(MatchmakingVisitor visitor) {
        visitor.visit(this);
    }
}