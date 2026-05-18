package it.polimi.ingsw.server.model.update;

import it.polimi.ingsw.common.network.dto.BoardDto;
import java.util.List;

/**
 * Server-side board update ready to be converted for network delivery.
 *
 * @param UpperRowCards visible upper-row card identifiers
 * @param LowerRowCards visible lower-row card identifiers
 * @param currentEra current era number
 * @param currentRound current round number
 */
public record BoardUpdate(List<Integer> UpperRowCards,
                          List<Integer> LowerRowCards,
                          int currentEra,
                          int currentRound,
                          Integer nextDeckEra) {
    /**
     * Converts this update to its network DTO.
     *
     * @return board DTO
     */
    public BoardDto toDTO() {
        return new BoardDto(UpperRowCards, LowerRowCards, currentEra, currentRound, nextDeckEra);
    }
}