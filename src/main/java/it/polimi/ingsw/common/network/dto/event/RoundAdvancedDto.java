package it.polimi.ingsw.common.network.dto.event;

import java.io.Serial;

import it.polimi.ingsw.common.visitor.EventVisitor;

/** Event DTO representing the advancement to a new round. */
public record RoundAdvancedDto(int newRound) implements GameEventDto {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Accepts a visitor. @param visitor visitor handling this event */
    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}
