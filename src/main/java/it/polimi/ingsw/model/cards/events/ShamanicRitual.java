package it.polimi.ingsw.model.cards.events;

import java.util.ArrayList;
import java.util.List;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.updates.GameEvent;

/**
 * "Shamanic Ritual" event.
 * <p>
 * For each player:
 * <ul>
 *     <li>Computes a score based on their stars and card effects.</li>
 *     <li>The player(s) with the highest score gain prestige points.</li>
 *     <li>The player(s) with the lowest score lose prestige points.</li>
 * </ul>
 * Card effects are applied both during score computation and after prestige changes
 * via the {@code onShamanicRitualEvent} hook.
 * <p>
 * In case of a complete tie (max = min), both bonus and penalty are applied,
 * as rules state that points are first added and then removed.
 */
public class ShamanicRitual extends Event {
    private final int incrPrestigePoints;
    private final int decrPrestigePoints; // negative number

    /**
     * Constructs the Shamanic Ritual event.
     *
     * @param idcard the card identifier
     * @param era the card era
     * @param incrPrestigePoints prestige points gained by highest score
     * @param decrPrestigePoints prestige points lost by lowest score (negative)
     */
    public ShamanicRitual(int idcard, int era, int incrPrestigePoints, int decrPrestigePoints) {
        super(idcard, era);
        this.incrPrestigePoints = incrPrestigePoints;
        this.decrPrestigePoints = decrPrestigePoints;
    }

    /**
     * Executes the event:
     * computes each player's score, determines max and min,
     * applies prestige changes, and notifies all cards.
     *
     * @param players list of involved players
     */
    @Override
    public List<GameEvent> execute(List<Player> players) {
        List<GameEvent> events = new ArrayList<>();
        int[] stars = new int[players.size()];
        int max = 0, min;

        // Calcolo delle stelle
        for (int i = 0; i < players.size(); i++) {
            stars[i] = players.get(i).getStarsNumber();
            for (Card card : players.get(i).getTribe()) {
                stars[i] += card.onShamanicRitualEvent(players.get(i), 0, 0);
            }
        }

        // Ricerca max e min
        min = stars[0];
        for (int x : stars) {
            max = Math.max(max, x);
            min = Math.min(min, x);
        }

        // Applicazione effetti
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            String reason = "Rituale Sciamanico: " + stars[i] + " stelle. Nessun bonus/malus.";

            if (stars[i] == max) {
                player.addPrestige(incrPrestigePoints);
                for (Card card : player.getTribe()) {
                    card.onShamanicRitualEvent(player, incrPrestigePoints, 0);
                }
                reason = "Rituale Sciamanico: " + stars[i] + " stelle (MAX). +" + incrPrestigePoints + " PP";
            }
            if (stars[i] == min) {
                player.addPrestige(decrPrestigePoints);
                for (Card card : player.getTribe()) {
                    card.onShamanicRitualEvent(player, 0, decrPrestigePoints);
                }
                if (max == min) {
                    reason += " e (MIN) " + decrPrestigePoints + " PP"; // Caso estremo di pareggio totale
                } else {
                    reason = "Rituale Sciamanico: " + stars[i] + " stelle (MIN). " + decrPrestigePoints + " PP";
                }
            }

            events.add(new GameEvent.PlayerResourcesChangedEvent(
                    player.getNickname(),
                    player.getFood(),
                    player.getPrestigePoints(),
                    player.getFoodDiscount(),
                    reason
            ));
        }
        return events;
    }
}