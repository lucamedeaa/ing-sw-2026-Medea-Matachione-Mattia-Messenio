package it.polimi.ingsw.common.network.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * One ranked row in a leaderboard snapshot.
 *
 * @param position ranking position, starting from 1
 * @param nickname player nickname
 * @param finalScore final score reached in the completed game
 * @param remainingFood food left at game end
 * @param playedAt completion timestamp used to display historical scores
 */
public record LeaderboardEntryDto(
        int position,
        String nickname,
        int finalScore,
        int remainingFood,
        LocalDateTime playedAt
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
