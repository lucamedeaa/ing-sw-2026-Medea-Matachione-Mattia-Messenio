package it.polimi.ingsw.common.message.server;

import java.io.Serial;

import it.polimi.ingsw.common.network.dto.PlayerGameCompletedDto;
import it.polimi.ingsw.common.visitor.ClientMessageVisitor;

/**
 * Immutable data transfer object for game completed message.
 *
 * @param completedGame completed game summary
 */
public record GameCompletedMessage(PlayerGameCompletedDto completedGame) implements ServerMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    /** {@inheritDoc} */
    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
