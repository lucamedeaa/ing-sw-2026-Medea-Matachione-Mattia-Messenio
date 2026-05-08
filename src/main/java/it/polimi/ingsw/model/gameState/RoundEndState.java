package it.polimi.ingsw.model.gameState;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.updates.AvailableAction.*;
import it.polimi.ingsw.model.updates.AvailableAction;
import it.polimi.ingsw.model.updates.GameEvent;
import it.polimi.ingsw.model.updates.GameEvent.*;


import java.util.List;

/** Game state handling end-of-round logic, including round progression, cleanup, and game termination check. */
public class RoundEndState extends GameState {

    /** Constructs the round end state. @param game the game instance */
    public RoundEndState(Game game) {
        super(game);
    }

    @Override
    public void start() {
        game.incrementRound();
        game.pushEvent(new RoundAdvancedEvent(game.getCurrentRound()));

        // Snapshot risorse pre-risoluzione
        java.util.Map<String, int[]> before = new java.util.HashMap<>();
        for (Player p : game.getPlayers()) {
            before.put(p.getNickname(), new int[]{p.getFood(), p.getPrestigePoints()});
        }

        if (isGameOver()) {
            List<GameEvent> finalEvents = game.getBoard().resolveFinalEvents(game.getPlayers());
            for (GameEvent e : finalEvents) {
                game.pushEvent(e);
            }
            notifyChanges(before, "Eventi di Fine Partita");
            this.transition(new ScoringState(this.game));
        } else {
            int eraBefore = game.getBoard().getCurrentEraNumber();

            List<GameEvent> resolutionEvents = game.getBoard().cleanupForNextRound(game.getPlayers());
            notifyBoardState();

            for (GameEvent e : resolutionEvents) {
                game.pushEvent(e);
            }

            int eraAfter = game.getBoard().getCurrentEraNumber();
            if (eraAfter > eraBefore) {
                game.pushEvent(new EraTransitionEvent(eraAfter));
            }

            notifyChanges(before, "Risorse ottenute a fine round (Bonus Totem Order Tile)");
            this.transition(new PlacementState(this.game));
        }
    }

    private void notifyChanges(java.util.Map<String, int[]> before, String reason) {
        for (Player p : game.getPlayers()) {
            // Invia l'evento per TUTTI, indipendentemente dai guadagni
            game.pushEvent(new PlayerResourcesChangedEvent(
                    p.getNickname(), p.getFood(), p.getPrestigePoints(), p.getFoodDiscount(), reason
            ));
        }
    }

    /** Checks whether the game has reached its end condition. @return true if the game is over */
    private boolean isGameOver() {
        return game.getCurrentRound() > 1;
    }

    @Override
    public List<AvailableAction> getAvailableActions(String playerNickname) {
        return List.of(); // Nessuna azione disponibile
    }

    @Override
    public String getActivePlayerNickname() {
        return null; // Nessun giocatore attivo
    }
/*
    private void notifyObserversPlayersResources() {
        for (it.polimi.ingsw.model.Player p : game.getPlayers()) {
            game.pushEvent(new PlayerResourcesChangedEvent(p.getNickname(), p.getFood(), p.getPrestigePoints(), "Risoluzione Eventi di Fine Round"));
        }
    }
*/
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