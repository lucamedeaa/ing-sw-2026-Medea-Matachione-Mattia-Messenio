package it.polimi.ingsw.server.model.state;

import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.CompletedGameResult;
import it.polimi.ingsw.server.model.PlayerGameResult;
import it.polimi.ingsw.server.model.update.AvailableAction;
import it.polimi.ingsw.server.model.update.GameEvent;
import it.polimi.ingsw.server.model.update.PlayerScoreUpdate;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Final game state responsible for computing scores and determining the winner. */
public class ScoringState extends GameState {

    /** Constructs the scoring state. @param game the game instance */
    public ScoringState(Game game) {
        super(game);
    }

    @Override
    public void start() {
        List<PlayerGameResult> leaderboard = calculateFinalScores();

        List<PlayerScoreUpdate> scores = leaderboard.stream()
                .map(r -> new PlayerScoreUpdate(r.nickname(), r.finalScore(), r.remainingFood()))
                .toList();
        game.pushEvent(new GameEvent.GameOverEvent(scores));

        game.completeNormally(new CompletedGameResult(leaderboard));
    }

    private List<PlayerGameResult> calculateFinalScores() {
        List<PlayerGameResult> orderedResults = game.getPlayers().stream()
                .map(player -> new PlayerGameResult(0, player.getNickname(), player.calculateTotalScore(), player.getFood()))
                .sorted(Comparator
                        .comparingInt(PlayerGameResult::finalScore).reversed()
                        .thenComparing(Comparator.comparingInt(PlayerGameResult::remainingFood).reversed())
                        .thenComparing(PlayerGameResult::nickname))
                .toList();

        List<PlayerGameResult> rankedResults = new ArrayList<>();
        int previousScore = Integer.MIN_VALUE;
        int previousFood = Integer.MIN_VALUE;
        int previousPosition = 0;
        for (int i = 0; i < orderedResults.size(); i++) {
            PlayerGameResult result = orderedResults.get(i);
            int position = result.finalScore() == previousScore && result.remainingFood() == previousFood
                    ? previousPosition
                    : i + 1;
            rankedResults.add(new PlayerGameResult(position, result.nickname(), result.finalScore(), result.remainingFood()));
            previousScore = result.finalScore();
            previousFood = result.remainingFood();
            previousPosition = position;
        }

        return rankedResults;
    }


    @Override
    public List<AvailableAction> getAvailableActions(String playerNickname) {
        return List.of();
    }

    @Override
    public String getActivePlayerNickname() {
        return null;
    }
}
