package it.polimi.ingsw.common.network.dto.event;

import java.io.Serial;

import it.polimi.ingsw.common.visitor.EventVisitor;

import java.util.List;

/** Event DTO indicating that a board row has been refilled with new cards. */
public record BoardRefilledDto(int row, List<Integer> newCardIds, Integer nextDeckEra) implements GameEventDto {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Ensures the list of card IDs is immutable. */
    public BoardRefilledDto {
        newCardIds = List.copyOf(newCardIds);
    }

    /** Accepts a visitor to handle the event. @param visitor processing the event */
    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}
