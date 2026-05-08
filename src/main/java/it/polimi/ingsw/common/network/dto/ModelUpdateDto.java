package it.polimi.ingsw.common.network.dto;

import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.common.network.dto.event.GameEventDto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record ModelUpdateDto(
        List<GameEventDto> events,
        String activePlayerNickname,
        List<ActionDto> activePlayerActions
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
