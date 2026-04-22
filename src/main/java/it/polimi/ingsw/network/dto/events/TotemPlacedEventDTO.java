package it.polimi.ingsw.network.dto.events;
import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.visitor.EventVisitor;

public record TotemPlacedEventDTO(String nickname, int positionIndex) implements GameEventDTO {
    @Override
    public void accept(EventVisitor visitor) { visitor.visit(this); }
}