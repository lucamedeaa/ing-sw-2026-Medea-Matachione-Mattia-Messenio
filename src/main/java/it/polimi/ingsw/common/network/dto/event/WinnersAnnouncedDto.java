package it.polimi.ingsw.common.network.dto.event;

import java.io.Serial;

import it.polimi.ingsw.common.visitor.EventVisitor;

import java.util.List;

/**
 * Event DTO announcing the winning player or tied players.
 *
 * @param winnersNicknames nicknames of all winners
 */
public record WinnersAnnouncedDto(List<String> winnersNicknames) implements GameEventDto {
    @Serial
    private static final long serialVersionUID = 1L;

    /** {@inheritDoc} */
    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}
