package it.polimi.ingsw.common.message.server;

import java.io.Serial;

import it.polimi.ingsw.common.network.dto.GameInfoDto;
import it.polimi.ingsw.common.visitor.ClientMessageVisitor;
import java.util.List;

/**
 * Immutable data transfer object for available games response message.
 *
 * @param games games
 */
public record AvailableGamesResponseMessage(List<GameInfoDto> games) implements ServerMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    /** {@inheritDoc} */
    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
