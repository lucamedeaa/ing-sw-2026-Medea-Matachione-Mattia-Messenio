package it.polimi.ingsw.network.dto.events;

import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.visitor.EventVisitor;

import java.util.List;

/** Event DTO indicating that a board row has been refilled with new cards. */
public record BoardRefilledEventDTO(int row, List<Integer> newCardIds) implements GameEventDTO {

    /** Ensures the list of card IDs is immutable. */
    public BoardRefilledEventDTO {
        newCardIds = List.copyOf(newCardIds);
    }

    /** Accepts a visitor to handle the event. @param visitor processing the event */
    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}