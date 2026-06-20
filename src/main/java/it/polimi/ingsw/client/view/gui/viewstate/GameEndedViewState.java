package it.polimi.ingsw.client.view.gui.viewstate;

import it.polimi.ingsw.common.network.dto.LeaderboardSnapshotDto;
import it.polimi.ingsw.common.network.dto.PlayerGameCompletedDto;
import it.polimi.ingsw.common.network.dto.PlayerScoreDto;
import java.util.List;

/**
 * Immutable snapshot rendered by the game-ended screen.
 *
 * @param sessionScores final scores for the just-completed match
 * @param localResult completion data for the local player
 * @param globalLeaderboard global leaderboard, or null while it is loading
 */
public record GameEndedViewState(
        List<PlayerScoreDto> sessionScores,
        PlayerGameCompletedDto localResult,
        LeaderboardSnapshotDto globalLeaderboard  // null = leaderboard not loaded yet (spinner shown)
) {
    /**
     * Copies score data into an immutable list.
     */
    public GameEndedViewState {
        sessionScores = (sessionScores != null) ? List.copyOf(sessionScores) : List.of();
    }
}
