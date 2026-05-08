package it.polimi.ingsw.network.dto.events;

import java.io.Serial;

import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.visitor.EventVisitor;

public record PlayerLeftGameDTO(String nickname) implements GameEventDTO {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}
