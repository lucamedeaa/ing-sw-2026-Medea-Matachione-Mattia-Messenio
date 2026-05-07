package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.dto.PlayerGameCompletedDTO;
import it.polimi.ingsw.network.visitor.ClientMessageVisitor;

public record GameCompletedMessage(PlayerGameCompletedDTO completedGame) implements ServerMessage {
    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
