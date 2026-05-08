package it.polimi.ingsw.server.leaderboard;

import it.polimi.ingsw.server.model.CompletedGameResult;
import it.polimi.ingsw.common.network.dto.LeaderboardEntryDto;
import it.polimi.ingsw.common.network.dto.LeaderboardSnapshotDto;

import java.util.List;

public interface LeaderboardService {
    List<LeaderboardEntryDto> recordCompletedGame(CompletedGameResult result);

    LeaderboardSnapshotDto getLeaderboard(int playerCount);
}
