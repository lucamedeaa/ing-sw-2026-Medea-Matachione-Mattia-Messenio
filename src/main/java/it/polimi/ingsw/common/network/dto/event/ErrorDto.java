package it.polimi.ingsw.common.network.dto.event;

import java.io.Serial;

import it.polimi.ingsw.common.message.server.ServerMessage;
import it.polimi.ingsw.common.visitor.ClientMessageVisitor;

/**
 * Server message carrying a recoverable error text.
 *
 * @param error error message to show to the client
 */
public record ErrorDto(String error) implements ServerMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    /** {@inheritDoc} */
    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
