package it.polimi.ingsw.server.model.update;

import it.polimi.ingsw.common.network.dto.ModelUpdateDto;
import java.util.List;

public record ModelUpdate(List<GameEvent> events,
                          String activePlayerNickname,
                          List<AvailableAction> activePlayerActions) {
    public ModelUpdateDto toDTO() {
        return new ModelUpdateDto(
                events.stream().map(GameEvent::toDTO).toList(),
                activePlayerNickname,
                activePlayerActions.stream().map(AvailableAction::toDTO).toList()
        );
    }
}