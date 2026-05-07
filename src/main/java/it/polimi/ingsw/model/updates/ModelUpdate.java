package it.polimi.ingsw.model.updates;

import it.polimi.ingsw.network.dto.ModelUpdateDTO;
import java.util.List;

public record ModelUpdate(List<GameEvent> events,
                          String activePlayerNickname,
                          List<AvailableAction> activePlayerActions) {
    public ModelUpdateDTO toDTO() {
        return new ModelUpdateDTO(
                events.stream().map(GameEvent::toDTO).toList(),
                activePlayerNickname,
                activePlayerActions.stream().map(AvailableAction::toDTO).toList()
        );
    }
}