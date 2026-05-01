package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.visitor.ClientMessageVisitor;

import java.util.List;

public record DeltaEventMessage(
        List<GameEventDTO> events,
        List<AvailableActionDTO> nextActions,
        String activePlayer
) implements ServerMessage {
    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}