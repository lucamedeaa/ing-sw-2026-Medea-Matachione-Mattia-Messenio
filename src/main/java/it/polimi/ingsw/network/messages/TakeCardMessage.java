package it.polimi.ingsw.network.messages;

public record TakeCardMessage(int row, int col) implements ClientMessage {
    @Override
    public void accept(ServerMessageVisitor visitor) {
        visitor.visit(this);
    }
}
