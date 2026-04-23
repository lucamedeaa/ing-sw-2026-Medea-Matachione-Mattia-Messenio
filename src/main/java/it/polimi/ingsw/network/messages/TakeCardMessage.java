package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.visitor.InGameVisitor;

public record TakeCardMessage(int row, int col) implements InGameMessage {
    @Override
    public void accept(InGameVisitor visitor) {
        visitor.visit(this);
    }
}