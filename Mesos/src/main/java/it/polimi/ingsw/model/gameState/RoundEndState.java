package it.polimi.ingsw.model.gameState;

import it.polimi.ingsw.model.Game;

public class RoundEndState extends GameState {

    public RoundEndState(Game game) {
        super(game);
    }

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

    private boolean isGameOver() {
        return game.getCurrentRound() > 10;
    }
}