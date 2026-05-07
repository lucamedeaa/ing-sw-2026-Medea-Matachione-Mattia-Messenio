package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.visitor.ClientMessageVisitor;

public record MatchmakingSuccessMessage(String text) implements ServerMessage {
    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
