package it.polimi.ingsw.common.message.server;

import java.io.Serial;

import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.common.network.dto.event.GameEventDto;
import it.polimi.ingsw.common.visitor.ClientMessageVisitor;

import java.util.List;

/**
 * Immutable data transfer object for delta event message.
 *
 * @param events events received from the server
 * @param nextActions actions available after the event
 * @param activePlayer active player nickname
 */
public record DeltaEventMessage(
        List<GameEventDto> events,
        List<ActionDto> nextActions,
        String activePlayer
) implements ServerMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    /** {@inheritDoc} */
    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
