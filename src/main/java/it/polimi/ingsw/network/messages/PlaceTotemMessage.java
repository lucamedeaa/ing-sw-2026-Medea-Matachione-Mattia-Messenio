package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.visitor.InGameVisitor;

public record PlaceTotemMessage(int positionIndex) implements InGameMessage {
    @Override
    public void accept(InGameVisitor visitor) {
        visitor.visit(this);
    }
}