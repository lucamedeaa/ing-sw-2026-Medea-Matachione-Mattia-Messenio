package it.polimi.ingsw.common.network.dto.event;

import java.io.Serial;

import it.polimi.ingsw.common.message.server.ServerMessage;
import it.polimi.ingsw.common.visitor.ClientMessageVisitor;

public record ErrorDto(String error) implements ServerMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
