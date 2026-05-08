package it.polimi.ingsw.network.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public record LeaderboardEntryDTO(
        int position,
        String nickname,
        int finalScore,
        int remainingFood,
        LocalDateTime playedAt
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
