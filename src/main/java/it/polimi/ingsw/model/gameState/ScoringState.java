package it.polimi.ingsw.model.gameState;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.CompletedGameResult;
import it.polimi.ingsw.model.PlayerGameResult;
import it.polimi.ingsw.model.updates.AvailableAction;


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
        return List.of(); // Nessuna azione disponibile
    }

    @Override
    public String getActivePlayerNickname() {
        return null; // Nessun giocatore attivo
    }
}
