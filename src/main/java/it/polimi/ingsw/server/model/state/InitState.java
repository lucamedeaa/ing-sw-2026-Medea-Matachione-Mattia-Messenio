package it.polimi.ingsw.server.model.state;

import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.update.AvailableAction;

import java.util.List;

/** Initial game state that immediately transitions to the placement phase. */
public class InitState extends GameState {

    /** Constructs the initial state. @param game the game instance */
    public InitState(Game game) {
        super(game);
    }

    /** Starts the game by transitioning to the placement state. */
    @Override
    public void start() {

        this.transition(new PlacementState(this.game));

        game.notifyFullSync();


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