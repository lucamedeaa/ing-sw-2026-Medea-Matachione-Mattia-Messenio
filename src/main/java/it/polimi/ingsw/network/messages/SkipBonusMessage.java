package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.visitor.InGameVisitor;

public record SkipBonusMessage() implements InGameMessage {
    @Override
    public void accept(InGameVisitor visitor) {
        visitor.visit(this);
    }
}