package it.polimi.ingsw.network.dto.events;
import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.visitor.EventVisitor;

public record CardAddedToTribeEventDTO(String nickname, String cardId) implements GameEventDTO {
    @Override
    public void accept(EventVisitor visitor) { visitor.visit(this); }
}