package it.polimi.ingsw.common.network.dto.event;

import java.io.Serial;

import it.polimi.ingsw.common.visitor.EventVisitor;

/** Event DTO representing a card being taken from the board. */
public record CardTakenDto(String nickname, int row, int col) implements GameEventDto {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Accepts a visitor. @param visitor */
    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}
