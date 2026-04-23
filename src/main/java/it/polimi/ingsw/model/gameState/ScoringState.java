package it.polimi.ingsw.model.gameState;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;

/** Final game state responsible for computing scores and determining the winner. */
public class ScoringState extends GameState {

    /** Constructs the scoring state. @param game the game instance */
    public ScoringState(Game game) {
        super(game);
    }

    /** Executes final scoring and determines the winner. */
    @Override
    public void start() {
        calculateFinalScores();
        Player winner = game.determineWinner();
        announceWinner(winner);
    }

    /** Computes final scores for all players. */
    private void calculateFinalScores() {
    }

    /** Handles winner announcement logic. @param winner the winning player */
    private void announceWinner(Player winner) {
    }
}