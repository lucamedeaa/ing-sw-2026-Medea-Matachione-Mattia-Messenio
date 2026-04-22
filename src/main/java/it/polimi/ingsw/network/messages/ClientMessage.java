package it.polimi.ingsw.network.messages;

import java.io.Serializable;

public interface ClientMessage extends Serializable {
    void accept(ServerMessageVisitor visitor);
}