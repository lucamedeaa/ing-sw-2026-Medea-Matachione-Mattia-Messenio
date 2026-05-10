package it.polimi.ingsw.server.model.update;

import it.polimi.ingsw.common.network.dto.ModelUpdateDto;
import java.util.List;

/**
 * Server-side incremental model update.
 *
 * @param events ordered events to deliver
 * @param activePlayerNickname player expected to act next
 * @param activePlayerActions actions available to the active player
 */
public record ModelUpdate(List<GameEvent> events,
                          String activePlayerNickname,
                          List<AvailableAction> activePlayerActions) {
    /**
     * Converts this update to its network DTO.
     *
     * @return model update DTO
     */
    public ModelUpdateDto toDTO() {
        return new ModelUpdateDto(
                events.stream().map(GameEvent::toDTO).toList(),
                activePlayerNickname,
                activePlayerActions.stream().map(AvailableAction::toDTO).toList()
        );
    }
}