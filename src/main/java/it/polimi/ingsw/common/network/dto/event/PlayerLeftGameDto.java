package it.polimi.ingsw.common.network.dto.event;

import java.io.Serial;

import it.polimi.ingsw.common.visitor.EventVisitor;

/**
 * Event DTO emitted when a player leaves and the match cannot continue.
 *
 * @param nickname nickname of the player who left
 */
public record PlayerLeftGameDto(String nickname) implements GameEventDto {
    @Serial
    private static final long serialVersionUID = 1L;

    /** {@inheritDoc} */
    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}
