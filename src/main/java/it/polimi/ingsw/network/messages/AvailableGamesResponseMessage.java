package it.polimi.ingsw.network.messages;

import java.io.Serial;

import it.polimi.ingsw.network.visitor.ClientMessageVisitor;
import java.util.List;

public record AvailableGamesResponseMessage(List<GameInfoDTO> games) implements ServerMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
