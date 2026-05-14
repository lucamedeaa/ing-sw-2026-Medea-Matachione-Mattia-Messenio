package it.polimi.ingsw.common.network.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record InitTurnOrderTileDto(List<String> nickPlayers) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
