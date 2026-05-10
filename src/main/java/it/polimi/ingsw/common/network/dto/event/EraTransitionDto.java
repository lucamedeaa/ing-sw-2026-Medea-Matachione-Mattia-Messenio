package it.polimi.ingsw.common.network.dto.event;

import java.io.Serial;

import it.polimi.ingsw.common.visitor.EventVisitor;

/** Event DTO representing a transition to a new era. */
public record EraTransitionDto(int newEraNumber) implements GameEventDto {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Accepts a visitor. @param visitor visitor handling this event */
    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }

}
