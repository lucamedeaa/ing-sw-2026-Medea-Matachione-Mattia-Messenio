package it.polimi.ingsw.network.dto.events;

import java.io.Serial;

import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.visitor.EventVisitor;

/** Event DTO representing a totem being placed on the offer track. */
public record TotemPlacedEventDTO(String nickname, int positionIndex) implements GameEventDTO {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Accepts a visitor. @param visitor */
    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}
