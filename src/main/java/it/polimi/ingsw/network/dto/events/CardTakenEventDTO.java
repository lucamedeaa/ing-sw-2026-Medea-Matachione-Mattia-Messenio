package it.polimi.ingsw.network.dto.events;
import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.visitor.EventVisitor;

public record CardTakenEventDTO(String nickname, int row, int col) implements GameEventDTO {
    @Override
    public void accept(EventVisitor visitor) { visitor.visit(this); }
}