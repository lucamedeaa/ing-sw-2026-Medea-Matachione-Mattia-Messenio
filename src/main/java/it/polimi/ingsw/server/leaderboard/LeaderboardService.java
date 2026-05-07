package it.polimi.ingsw.server.leaderboard;

import it.polimi.ingsw.model.CompletedGameResult;
import it.polimi.ingsw.network.dto.LeaderboardEntryDTO;
import it.polimi.ingsw.network.dto.LeaderboardSnapshot;

import java.util.List;

public interface LeaderboardService {
    List<LeaderboardEntryDTO> recordCompletedGame(CompletedGameResult result);

    LeaderboardSnapshot getLeaderboard(int playerCount);
}
