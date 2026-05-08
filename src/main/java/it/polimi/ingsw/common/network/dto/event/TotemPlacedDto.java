package it.polimi.ingsw.common.network.dto.event;

import java.io.Serial;

import it.polimi.ingsw.common.visitor.EventVisitor;

/** Event DTO representing a totem being placed on the offer track. */
public record TotemPlacedDto(String nickname, int positionIndex) implements GameEventDto {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Accepts a visitor. @param visitor */
    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}
