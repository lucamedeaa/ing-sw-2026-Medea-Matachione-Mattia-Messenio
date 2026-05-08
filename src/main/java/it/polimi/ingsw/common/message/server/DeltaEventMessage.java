package it.polimi.ingsw.common.message.server;

import java.io.Serial;

import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.common.network.dto.event.GameEventDto;
import it.polimi.ingsw.common.visitor.ClientMessageVisitor;

import java.util.List;

public record DeltaEventMessage(
        List<GameEventDto> events,
        List<ActionDto> nextActions,
        String activePlayer
) implements ServerMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
