package it.polimi.ingsw.network.messages;

import java.io.Serializable;

public interface ServerMessage extends Serializable {
    void accept(ClientMessageVisitor visitor);
}