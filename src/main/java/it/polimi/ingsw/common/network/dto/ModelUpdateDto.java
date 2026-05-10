package it.polimi.ingsw.common.network.dto;

import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.common.network.dto.event.GameEventDto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Delta update sent after model changes.
 *
 * @param events ordered events that clients must apply
 * @param activePlayerNickname player expected to act after the update
 * @param activePlayerActions actions available to the active player
 */
public record ModelUpdateDto(
        List<GameEventDto> events,
        String activePlayerNickname,
        List<ActionDto> activePlayerActions
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
