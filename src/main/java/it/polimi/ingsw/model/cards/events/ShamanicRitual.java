package it.polimi.ingsw.model.cards.events;

import java.util.List;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;

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
    public void execute(List<Player> players) {

        int[] stars = new int[players.size()];
        int max = 0, min;

        for (int i = 0; i < players.size(); i++) {
            stars[i] = players.get(i).getStarsNumber();
            for (Card card : players.get(i).getTribe()) {
                stars[i] += card.onShamanicRitualEvent(players.get(i), 0, 0);
            }
        }

        min = stars[0];
        for (int x : stars) {
            max = Math.max(max, x);
            min = Math.min(min, x);
        }

        for (int i = 0; i < players.size(); i++) {
            if (stars[i] == max) {
                players.get(i).addPrestige(incrPrestigePoints);
                for (Card card : players.get(i).getTribe()) {
                    card.onShamanicRitualEvent(players.get(i), incrPrestigePoints, 0);
                }
            }
            if (stars[i] == min) {
                players.get(i).addPrestige(decrPrestigePoints);
                for (Card card : players.get(i).getTribe()) {
                    card.onShamanicRitualEvent(players.get(i), 0, decrPrestigePoints);
                }
            }
        }
    }
}