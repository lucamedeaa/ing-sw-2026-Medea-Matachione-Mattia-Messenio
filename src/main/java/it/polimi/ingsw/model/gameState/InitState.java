package it.polimi.ingsw.model.gameState;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.events.*;

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
        game.notifyAll(new RoundAdvancedEventDTO(game.getCurrentRound()));

        //il model dovrebbe inizializzare a 0 food e 0 prestige i player
        for (it.polimi.ingsw.model.Player p : game.getPlayers()) {
            game.notifyAll(new PlayerResourcesChangedEventDTO(
                p.getNickname(),
                p.getFood(),
                p.getPrestigePoints()
            ));
        }

        List<Integer> upperIds = game.getBoard().getRow(0).stream()
                .map(opt -> opt.map(Card::getIDcard).orElse(null))
                .toList();
        List<Integer> lowerIds = game.getBoard().getRow(1).stream()
                .map(opt -> opt.map(Card::getIDcard).orElse(null))
                .toList();

        game.notifyAll(new BoardRefilledEventDTO(0, upperIds));
        game.notifyAll(new BoardRefilledEventDTO(1, lowerIds));


        this.transition(new PlacementState(this.game));

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