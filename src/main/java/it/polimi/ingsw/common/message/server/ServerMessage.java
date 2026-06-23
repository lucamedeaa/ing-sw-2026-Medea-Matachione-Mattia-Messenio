package it.polimi.ingsw.common.message.server;

import java.io.Serializable;
import it.polimi.ingsw.common.visitor.ClientMessageVisitor;

/** Defines the contract for server message. */
public interface ServerMessage extends Serializable {
    void accept(ClientMessageVisitor visitor);
}