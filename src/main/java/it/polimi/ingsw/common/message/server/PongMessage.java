package it.polimi.ingsw.common.message.server;

import java.io.Serial;

import it.polimi.ingsw.common.visitor.ClientMessageVisitor;

public record PongMessage() implements ServerMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void accept(ClientMessageVisitor view) {
    }
}
