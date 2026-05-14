package it.polimi.ingsw.server.model.update;

import it.polimi.ingsw.common.network.dto.InitTurnOrderTileDto;
import it.polimi.ingsw.server.model.Player;

import java.util.List;

import static java.util.stream.Collectors.toList;

public record InitTurnOrderTileUpdate(List<String> players) {

    public InitTurnOrderTileDto toDto() {
        return new InitTurnOrderTileDto(players);
    }
}
