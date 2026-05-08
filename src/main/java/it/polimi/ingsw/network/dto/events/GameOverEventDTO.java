package it.polimi.ingsw.network.dto.events;

import java.io.Serial;

import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.dto.PlayerScoreDTO;
import it.polimi.ingsw.network.visitor.EventVisitor;
import java.util.List;

public record GameOverEventDTO(List<PlayerScoreDTO> leaderboard) implements GameEventDTO {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}
