package it.polimi.ingsw.server.model.card.event;

import java.util.ArrayList;
import java.util.List;

import it.polimi.ingsw.common.config.CardRegistry;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.Card;
import it.polimi.ingsw.server.model.update.GameEvent;

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
    private final int decrPrestigePoints;

    /**
     * Constructs the Shamanic Ritual event.
     *
     * @param idcard the card identifier
     */
    public ShamanicRitual(int idcard) {
        super(idcard);
        var info = CardRegistry.getCard(idcard);
        this.incrPrestigePoints = info.val1();
        this.decrPrestigePoints = info.val2();
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

        // Compute ritual scores
        for (int i = 0; i < players.size(); i++) {
            stars[i] = players.get(i).getStarsNumber();
            for (Card card : players.get(i).getTribe()) {
                stars[i] += card.onShamanicRitualEvent(players.get(i), 0, 0);
            }
        }

        // Find highest and lowest scores
        min = stars[0];
        for (int x : stars) {
            max = Math.max(max, x);
            min = Math.min(min, x);
        }

        // Apply bonuses and penalties
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            String reason = "Shamanic ritual: " + stars[i] + " stars. No bonuses or penalties.";

            if (stars[i] == max) {
                player.addPrestige(incrPrestigePoints);
                for (Card card : player.getTribe()) {
                    card.onShamanicRitualEvent(player, incrPrestigePoints, 0);
                }
                reason = "Shamanic ritual: " + stars[i] + " stars (MAX). +" + incrPrestigePoints + " PP";
            }
            if (stars[i] == min) {
                player.addPrestige(decrPrestigePoints);
                for (Card card : player.getTribe()) {
                    card.onShamanicRitualEvent(player, 0, decrPrestigePoints);
                }
                if (max == min) {
                    reason += " and (MIN) " + decrPrestigePoints + " PP"; // Complete tie case
                } else {
                    reason = "Shamanic ritual: " + stars[i] + " stars (MIN). " + decrPrestigePoints + " PP";
                }
            }

            events.add(new GameEvent.PlayerResourcesChangedEvent(
                    player.getNickname(),
                    player.getFood(),
                    player.getPrestigePoints(),
                    player.getFoodDiscount(),
                    player.getSustenanceDiscount(),
                    reason
            ));
        }
        return events;
    }
}