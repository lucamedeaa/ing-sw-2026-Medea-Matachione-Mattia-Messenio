package it.polimi.ingsw.client.view.gui.viewstate;

import it.polimi.ingsw.common.network.dto.LeaderboardSnapshotDto;
import it.polimi.ingsw.common.network.dto.PlayerGameCompletedDto;
import it.polimi.ingsw.common.network.dto.PlayerScoreDto;
import java.util.List;

public record GameEndedViewState(
        List<PlayerScoreDto> sessionScores,
        PlayerGameCompletedDto localResult,
        LeaderboardSnapshotDto globalLeaderboard  // null = spinner visibile
) {
    public GameEndedViewState {
        sessionScores = (sessionScores != null) ? List.copyOf(sessionScores) : List.of();
    }
}