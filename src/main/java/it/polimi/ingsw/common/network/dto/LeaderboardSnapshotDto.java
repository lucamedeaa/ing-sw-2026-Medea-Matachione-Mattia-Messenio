package it.polimi.ingsw.common.network.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Immutable leaderboard view for games with the same player count.
 *
 * @param playerCount number of players of the leaderboard category
 * @param entries ranked leaderboard entries
 */
public record LeaderboardSnapshotDto(int playerCount, List<LeaderboardEntryDto> entries) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Creates an immutable leaderboard snapshot.
     *
     * @param playerCount number of players of the leaderboard category
     * @param entries ranked leaderboard entries
     */
    public LeaderboardSnapshotDto {
        entries = List.copyOf(entries);
    }
}
