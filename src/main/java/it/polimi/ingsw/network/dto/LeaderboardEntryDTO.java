package it.polimi.ingsw.network.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record LeaderboardEntryDTO(
        int position,
        String nickname,
        int finalScore,
        int remainingFood,
        LocalDateTime playedAt
) implements Serializable {
}
