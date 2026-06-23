package it.polimi.ingsw.common.network.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Immutable data transfer object for init turn order tile dto.
 *
 * @param nickPlayers nick players
 */
public record InitTurnOrderTileDto(List<String> nickPlayers) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
