package it.polimi.ingsw.network.dto;

import java.io.Serializable;
import java.util.List;

public record ModelUpdateDTO(
        List<GameEventDTO> events,
        String activePlayerNickname,
        List<AvailableActionDTO> activePlayerActions
) implements Serializable {}