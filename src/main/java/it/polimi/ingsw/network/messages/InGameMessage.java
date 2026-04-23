package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.visitor.InGameVisitor;

public interface InGameMessage extends ClientMessage {
    void accept(InGameVisitor visitor);
}