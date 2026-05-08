package it.polimi.ingsw.network.dto;

import java.io.Serial;
import java.io.Serializable;

public record PlayerGameCompletedDTO(
        int playerCount,
        int localPosition,
        int localScore,
        int localRemainingFood,
        int globalPersonalBestPosition,
        int personalBestScore,
        int personalBestRemainingFood
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
