package it.polimi.ingsw.model.updates;

import it.polimi.ingsw.network.dto.BoardDTO;
import java.util.List;

public record BoardUpdate(List<Integer> UpperRowCards,
                          List<Integer> LowerRowCards,
                          int currentEra,
                          int currentRound) {
    public BoardDTO toDTO() {
        return new BoardDTO(UpperRowCards, LowerRowCards, currentEra, currentRound);
    }
}