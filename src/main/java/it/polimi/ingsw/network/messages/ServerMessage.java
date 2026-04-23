package it.polimi.ingsw.network.messages;

import java.io.Serializable;
import it.polimi.ingsw.network.visitor.ClientMessageVisitor;

public interface ServerMessage extends Serializable {
    void accept(ClientMessageVisitor visitor);
}