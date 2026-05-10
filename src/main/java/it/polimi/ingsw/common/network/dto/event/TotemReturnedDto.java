package it.polimi.ingsw.common.network.dto.event;

import java.io.Serial;

import it.polimi.ingsw.common.visitor.EventVisitor;

/**
 * Event DTO emitted when a player's totem returns to the turn-order track.
 *
 * @param nickname player whose totem returned
 * @param returnIndex zero-based return position on the next-round track
 */
public record TotemReturnedDto(String nickname, int returnIndex) implements GameEventDto {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}
