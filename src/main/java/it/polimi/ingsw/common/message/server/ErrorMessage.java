package it.polimi.ingsw.common.message.server;

import java.io.Serial;

import it.polimi.ingsw.common.visitor.ClientMessageVisitor;

public record ErrorMessage(String error) implements ServerMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
