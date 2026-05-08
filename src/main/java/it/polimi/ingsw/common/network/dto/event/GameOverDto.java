package it.polimi.ingsw.common.network.dto.event;

import java.io.Serial;

import it.polimi.ingsw.common.network.dto.PlayerScoreDto;
import it.polimi.ingsw.common.visitor.EventVisitor;
import java.util.List;

public record GameOverDto(List<PlayerScoreDto> leaderboard) implements GameEventDto {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}
