package it.polimi.ingsw.server.model.state;

import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.Card;
import it.polimi.ingsw.server.model.update.AvailableAction;
import it.polimi.ingsw.server.model.update.GameEvent;
import it.polimi.ingsw.server.model.update.GameEvent.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Game state handling end-of-round logic, including round progression, cleanup, and game termination check. */
public class RoundEndState extends GameState {

    /** Constructs the round end state. @param game the game instance */
    public RoundEndState(Game game) {
        super(game);
    }

    @Override
    public void start() {


        // Snapshot risorse pre-risoluzione
        Map<String, int[]> before = new HashMap<>();
        for (Player p : game.getPlayers()) {
            before.put(p.getNickname(), new int[]{p.getFood(), p.getPrestigePoints()});
        }

        if (isGameOver()) {
            List<GameEvent> finalEvents = game.getBoard().resolveFinalEvents(game.getPlayers());
            for (GameEvent e : finalEvents) {
                game.pushEvent(e);
            }
            notifyChanges(before, "Endgame Events");
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

            notifyChanges(before, "Resources obtained at the end of the round (Bonus Totem Order Tile)");
            game.incrementRound();
            game.pushEvent(new RoundAdvancedEvent(game.getCurrentRound()));
            this.transition(new PlacementState(this.game));
        }
    }

    private void notifyChanges(java.util.Map<String, int[]> before, String reason) {
        for (Player p : game.getPlayers()) {
            // Invia l'evento per TUTTI, indipendentemente dai guadagni
            game.pushEvent(new PlayerResourcesChangedEvent(
                    p.getNickname(), p.getFood(), p.getPrestigePoints(), p.getFoodDiscount(), p.getSustenanceDiscount(),reason
            ));
        }
    }

    /** Checks whether the game has reached its end condition. @return true if the game is over */
    private boolean isGameOver() {
        return game.getCurrentRound() >= 1;
    }

    @Override
    public List<AvailableAction> getAvailableActions(String playerNickname) {
        return List.of(); // Nessuna azione disponibile
    }

    @Override
    public String getActivePlayerNickname() {
        return null; // Nessun giocatore attivo
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