package it.polimi.ingsw.network.dto.events;

import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.visitor.EventVisitor;

import java.util.List;

public record WinnersAnnouncedEventDTO(List<String> winnersNicknames) implements GameEventDTO {
    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}