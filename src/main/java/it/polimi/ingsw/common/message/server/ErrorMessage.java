package it.polimi.ingsw.common.message.server;

import java.io.Serial;

import it.polimi.ingsw.common.visitor.ClientMessageVisitor;

/**
 * Immutable data transfer object for error message.
 *
 * @param error error message
 */
public record ErrorMessage(String error) implements ServerMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    /** {@inheritDoc} */
    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
