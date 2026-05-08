package it.polimi.ingsw.common.network.dto.event;

import java.io.Serial;

import it.polimi.ingsw.common.visitor.EventVisitor;

public record TotemReturnedDto(String nickname, int returnIndex) implements GameEventDto {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}
