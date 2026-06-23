package it.polimi.ingsw.server.model.update;

import it.polimi.ingsw.common.network.dto.InitTurnOrderTileDto;
import it.polimi.ingsw.server.model.Player;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Immutable value object for init turn order tile update.
 *
 * @param players players involved in the update
 */
public record InitTurnOrderTileUpdate(List<String> players) {

    /**
     * Returns the to dto.
     *
     * @return the converted to dto
     */
    public InitTurnOrderTileDto toDto() {
        return new InitTurnOrderTileDto(players);
    }
}
