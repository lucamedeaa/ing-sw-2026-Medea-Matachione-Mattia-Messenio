package it.polimi.ingsw.model.updates;



import it.polimi.ingsw.network.dto.GameEventDTO;

import java.util.List;

public record ModelUpdate(List<GameEvent> events,
                          String activePlayerNickname,
                          List<AvailableAction> activePlayerActions) {
}
