package it.polimi.ingsw.server.leaderboard;

import it.polimi.ingsw.server.model.CompletedGameResult;
import it.polimi.ingsw.server.model.PlayerGameResult;
import it.polimi.ingsw.common.network.dto.LeaderboardEntryDto;
import it.polimi.ingsw.common.network.dto.LeaderboardSnapshotDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InMemoryLeaderboardService implements LeaderboardService {
    private static final Comparator<SavedGameResult> BEST_RESULT_ORDER = Comparator
            .comparingInt(SavedGameResult::finalScore).reversed()
            .thenComparing(Comparator.comparingInt(SavedGameResult::remainingFood).reversed())
            .thenComparing(SavedGameResult::playedAt)
            .thenComparing(SavedGameResult::nickname);

    private final List<SavedGameResult> savedResults = new ArrayList<>();

    @Override
    public synchronized List<LeaderboardEntryDto> recordCompletedGame(CompletedGameResult result) {
        LocalDateTime playedAt = LocalDateTime.now();
        int playerCount = result.playerResults().size();
        Set<String> completedPlayerNicknames = new LinkedHashSet<>();
        for (PlayerGameResult playerResult : result.playerResults()) {
            completedPlayerNicknames.add(playerResult.nickname());
            savedResults.add(new SavedGameResult(
                    playerResult.nickname(),
                    playerResult.finalScore(),
                    playerResult.remainingFood(),
                    playedAt,
                    playerCount
            ));
        }
        return personalBestLeaderboardEntries(playerCount).stream()
                .filter(entry -> completedPlayerNicknames.contains(entry.nickname()))
                .toList();
    }

    @Override
    public synchronized LeaderboardSnapshotDto getLeaderboard(int playerCount) {
        return new LeaderboardSnapshotDto(playerCount, personalBestLeaderboardEntries(playerCount));
    }

    private List<LeaderboardEntryDto> personalBestLeaderboardEntries(int playerCount) {
        Map<String, SavedGameResult> personalBestByNickname = savedResults.stream()
                .filter(result -> result.playerCount() == playerCount)
                .collect(Collectors.toMap(
                        SavedGameResult::nickname,
                        Function.identity(),
                        this::betterPersonalBest
                ));

        List<SavedGameResult> sorted = personalBestByNickname.values().stream()
                .sorted(BEST_RESULT_ORDER)
                .toList();

        List<LeaderboardEntryDto> entries = new ArrayList<>();
        int previousScore = Integer.MIN_VALUE;
        int previousFood = Integer.MIN_VALUE;
        int previousPosition = 0;
        for (int i = 0; i < sorted.size(); i++) {
            SavedGameResult result = sorted.get(i);
            int position = result.finalScore() == previousScore && result.remainingFood() == previousFood
                    ? previousPosition
                    : i + 1;
            entries.add(new LeaderboardEntryDto(
                    position,
                    result.nickname(),
                    result.finalScore(),
                    result.remainingFood(),
                    result.playedAt()
            ));
            previousScore = result.finalScore();
            previousFood = result.remainingFood();
            previousPosition = position;
        }
        return entries;
    }

    private SavedGameResult betterPersonalBest(SavedGameResult first, SavedGameResult second) {
        return BEST_RESULT_ORDER.compare(first, second) <= 0 ? first : second;
    }

    private record SavedGameResult(
            String nickname,
            int finalScore,
            int remainingFood,
            LocalDateTime playedAt,
            int playerCount
    ) {
    }
}
