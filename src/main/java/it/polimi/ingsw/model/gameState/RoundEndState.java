package it.polimi.ingsw.model.gameState;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.updates.AvailableAction.*;
import it.polimi.ingsw.model.updates.AvailableAction;
import it.polimi.ingsw.model.updates.GameEvent.*;


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

        game.pushEvent(new RoundAdvancedEvent(game.getCurrentRound()));

        if (isGameOver()) {
            game.getBoard().resolveFinalEvents(game.getPlayers());

            notifyObserversPlayersResources();

            this.transition(new ScoringState(this.game));
        } else {

            int eraBefore = game.getBoard().getCurrentEraNumber();

            game.getBoard().cleanupForNextRound(game.getPlayers());

            int eraAfter = game.getBoard().getCurrentEraNumber();

            if (eraAfter > eraBefore) {
                game.pushEvent(new EraTransitionEvent(eraAfter));
            }

            notifyObserversPlayersResources();

            notifyBoardState();

            this.transition(new PlacementState(this.game));
        }
    }

    /** Checks whether the game has reached its end condition. @return true if the game is over */
    private boolean isGameOver() {
        return game.getCurrentRound() > 10;
    }

    @Override
    public List<AvailableAction> getAvailableActions(String playerNickname) {
        return List.of(); // Nessuna azione disponibile
    }

    @Override
    public String getActivePlayerNickname() {
        return null; // Nessun giocatore attivo
    }

    private void notifyObserversPlayersResources() {
        for (it.polimi.ingsw.model.Player p : game.getPlayers()) {
            game.pushEvent(new PlayerResourcesChangedEvent(
                p.getNickname(),
                p.getFood(),
                p.getPrestigePoints()
            ));
        }
    }

    private void notifyBoardState() {
        // Estraiamo gli ID usando il tuo nuovo metodo getIDcard()
        List<Integer> upperIds = game.getBoard().getRow(0).stream()
                .map(opt -> opt.map(Card::getIDcard).orElse(null))
                .toList();
        List<Integer> lowerIds = game.getBoard().getRow(1).stream()
                .map(opt -> opt.map(Card::getIDcard).orElse(null))
                .toList();

        game.pushEvent(new BoardRefilledEvent(0, upperIds));
        game.pushEvent(new BoardRefilledEvent(1, lowerIds));
    }
}