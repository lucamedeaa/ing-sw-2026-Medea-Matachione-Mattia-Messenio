package it.polimi.ingsw.model.gameState;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.PlayerScoreDTO;
import it.polimi.ingsw.network.dto.events.GameOverEventDTO;
import it.polimi.ingsw.network.dto.events.PlayerResourcesChangedEventDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Final game state responsible for computing scores and determining the winner. */
public class ScoringState extends GameState {

    /** Constructs the scoring state. @param game the game instance */
    public ScoringState(Game game) {
        super(game);
    }

    @Override
    public void start() {
        calculateFinalScores();
        announceWinner();
    }

    private void calculateFinalScores() {
        for (Player player : game.getPlayers()) {
            game.notifyObservers(new PlayerResourcesChangedEventDTO(
                    player.getNickname(),
                    player.getFood(),
                    player.calculateTotalScore()
            ));
        }
    }

    private void announceWinner() {
        List<PlayerScoreDTO> leaderboard = game.getPlayers().stream()
                .map(p -> new PlayerScoreDTO(p.getNickname(), p.calculateTotalScore(), p.getFood()))
                .sorted((p1, p2) -> {
                    int scoreCompare = Integer.compare(p2.finalScore(), p1.finalScore());
                    if (scoreCompare != 0) return scoreCompare;

                    return Integer.compare(p2.remainingFood(), p1.remainingFood());
                })
                .toList();


        game.notifyObservers(new GameOverEventDTO(leaderboard));
    }
    @Override
    public List<AvailableActionDTO> getAvailableActions(String playerNickname) {
        return List.of(); // Nessuna azione disponibile
    }

    @Override
    public String getActivePlayerNickname() {
        return null; // Nessun giocatore attivo
    }
}