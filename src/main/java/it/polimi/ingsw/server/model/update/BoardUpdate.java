package it.polimi.ingsw.server.model.update;

import it.polimi.ingsw.common.network.dto.BoardDto;
import java.util.List;

public record BoardUpdate(List<Integer> UpperRowCards,
                          List<Integer> LowerRowCards,
                          int currentEra,
                          int currentRound) {
    public BoardDto toDTO() {
        return new BoardDto(UpperRowCards, LowerRowCards, currentEra, currentRound);
    }
}