package it.polimi.ingsw.network.messages;

public record PlaceTotemMessage(int positionIndex) implements ClientMessage {
    @Override
    public void accept(ServerMessageVisitor visitor) {
        visitor.visit(this);
    }
}