package it.polimi.ingsw.network.messages;

public record SkipBonusMessage() implements ClientMessage {
    @Override
    public void accept(ServerMessageVisitor visitor) {
        visitor.visit(this);
    }
}