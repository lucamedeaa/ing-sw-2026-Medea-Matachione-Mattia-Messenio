package it.polimi.ingsw.network.dto.events;
import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.visitor.EventVisitor;
import java.util.List;

public record BoardRefilledEventDTO(int row, List<Integer> newCardIds) implements GameEventDTO {
    public BoardRefilledEventDTO {
        newCardIds = List.copyOf(newCardIds); // Rende la lista immutabile
    }
    @Override
    public void accept(EventVisitor visitor) { visitor.visit(this); }
}