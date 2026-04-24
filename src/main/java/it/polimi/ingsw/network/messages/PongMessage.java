package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.visitor.ClientMessageVisitor;

import java.io.Serializable;

public record PongMessage() implements ServerMessage, Serializable {
    @Override
    public void accept(ClientMessageVisitor view) {
    }
}