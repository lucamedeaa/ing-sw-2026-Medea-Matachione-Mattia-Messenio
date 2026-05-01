package it.polimi.ingsw.model.gameState;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.updates.AvailableAction;
import it.polimi.ingsw.model.updates.GameEvent.*;


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
        List<Player> winners = determineWinners();
        announceWinners(winners);
    }

    private void calculateFinalScores() {
        for (Player player : game.getPlayers()) {
            game.pushEvent(new PlayerResourcesChangedEvent(player.getNickname(), player.getFood(), player.calculateTotalScore(), "Calcolo Punteggio Finale"));
        }
    }

    private void announceWinners(List<Player> winners) {
        List<String> winnerNicknames = winners.stream()
                .map(Player::getNickname)
                .toList();

        game.pushEvent(new WinnersAnnouncedEvent(winnerNicknames));
    }

    private List<Player> determineWinners() {
        List<Player> players = game.getPlayers();
        if (players == null || players.isEmpty()) return List.of();

        int maxScore = players.stream()
                .mapToInt(Player::calculateTotalScore)
                .max()
                .orElse(0);

        List<Player> topScorers = players.stream()
                .filter(p -> p.calculateTotalScore() == maxScore)
                .toList();

        int maxFood = topScorers.stream()
                .mapToInt(Player::getFood)
                .max()
                .orElse(0);

        return topScorers.stream()
                .filter(p -> p.getFood() == maxFood)
                .toList();
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