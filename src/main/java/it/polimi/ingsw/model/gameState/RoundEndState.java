package it.polimi.ingsw.model.gameState;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.network.dto.AvailableActionDTO;

import java.util.List;

/** Game state handling end-of-round logic, including round progression, cleanup, and game termination check. */
public class RoundEndState extends GameState {

    /** Constructs the round end state. @param game the game instance */
    public RoundEndState(Game game) {
        super(game);
    }

    /** Advances the round, checks for game end, and transitions to the next appropriate state. */
    @Override
    public void start() {
        game.incrementRound();

        if (isGameOver()) {
            game.getBoard().resolveFinalEvents(game.getPlayers());
            this.transition(new ScoringState(this.game));
        } else {
            game.getBoard().cleanupForNextRound(game.getPlayers());
            this.transition(new PlacementState(this.game));
        }
    }

    /** Checks whether the game has reached its end condition. @return true if the game is over */
    private boolean isGameOver() {
        return game.getCurrentRound() > 10;
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