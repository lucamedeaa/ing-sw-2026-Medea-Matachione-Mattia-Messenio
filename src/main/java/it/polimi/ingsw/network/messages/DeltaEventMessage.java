package it.polimi.ingsw.network.messages;

import java.io.Serial;

import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.visitor.ClientMessageVisitor;

import java.util.List;

public record DeltaEventMessage(
        List<GameEventDTO> events,
        List<AvailableActionDTO> nextActions,
        String activePlayer
) implements ServerMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
