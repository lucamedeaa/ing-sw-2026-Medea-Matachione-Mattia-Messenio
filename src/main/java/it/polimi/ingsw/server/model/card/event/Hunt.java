package it.polimi.ingsw.server.model.card.event;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.Card;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.update.GameEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * "Hunt" event.
 * <p>
 * For each player:
 * <ul>
 *     <li>Grants food proportional to the number of HUNTER characters.</li>
 *     <li>Grants prestige points proportional to the number of HUNTER characters.</li>
 * </ul>
 * After applying the effect, triggers the {@code onHuntEvent}
 * hook on all cards in the player's tribe.
 */
public class Hunt extends Event {
    private final int foodGiven;
    private final int prestigeGiven;

    /**
     * Constructs the Hunt event.
     *
     * @param idcard the card identifier
     * @param era the card era
     * @param foodGiven food gained per HUNTER
     * @param prestigeGiven prestige gained per HUNTER
     */
    public Hunt(int idcard, int era, int foodGiven, int prestigeGiven) {
        super(idcard, era);
        this.foodGiven = foodGiven;
        this.prestigeGiven = prestigeGiven;
    }

    /**
     * Executes the event on all players:
     * grants food and prestige based on HUNTER characters
     * and notifies all cards in the player's tribe.
     *
     * @param players list of involved players
     */
    @Override
    public List<GameEvent> execute(List<Player> players) {
        List<GameEvent> events = new ArrayList<>();
        for (Player player : players) {
            int num = player.countCharactersOfType(CharacterType.HUNTER);
            player.addFood(num * foodGiven);
            player.addPrestige(num * prestigeGiven);

            events.add(new GameEvent.PlayerResourcesChangedEvent(
                    player.getNickname(),
                    player.getFood(),
                    player.getPrestigePoints(),
                    player.getFoodDiscount(),
                    player.getPrestigePoints(),
                    "Evento Caccia: +" + (num * foodGiven) + " cibo, +" + (num * prestigeGiven) + " PP"
            ));

            for (Card card : player.getTribe()) {
                card.onHuntEvent(player);
            }
        }
        return events;
    }
}