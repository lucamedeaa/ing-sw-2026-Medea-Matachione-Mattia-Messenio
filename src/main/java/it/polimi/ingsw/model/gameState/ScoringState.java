package it.polimi.ingsw.model.gameState;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;

public class ScoringState extends GameState {

    public ScoringState(Game game) {
        super(game);
    }

    @Override
    public void start() {
        calculateFinalScores();
        Player winner = game.determineWinner();
        announceWinner(winner);
    }

    private void calculateFinalScores() {
    }

    private void announceWinner(Player winner) {
    }
}