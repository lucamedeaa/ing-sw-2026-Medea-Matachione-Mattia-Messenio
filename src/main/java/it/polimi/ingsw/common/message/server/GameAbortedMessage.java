package it.polimi.ingsw.common.message.server;

import java.io.Serial;

import it.polimi.ingsw.common.visitor.ClientMessageVisitor;

/**
 * Immutable data transfer object for game aborted message.
 *
 * @param reason reason text
 */
public record GameAbortedMessage(String reason) implements ServerMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    /** {@inheritDoc} */
    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
