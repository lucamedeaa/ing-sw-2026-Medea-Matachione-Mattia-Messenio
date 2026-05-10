package it.polimi.ingsw.server.model.card.event;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.Card;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.update.GameEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * "Cave Paintings" event.
 * <p>
 * For each player:
 * <ul>
 *     <li>If the number of ARTIST characters is below a threshold, they lose prestige points.</li>
 *     <li>Otherwise, they gain prestige points proportional to the number of ARTIST characters.</li>
 * </ul>
 * After applying the effect, triggers the {@code onCavePaintingsEvent}
 * hook on all cards in the player's tribe.
 */
public class CavePaintings extends Event {
    private final int upperNumArtists;
    private final int decrPrestigePoints;
    private final int incrPrestigePoints;


    /**
     * Constructs the Cave Paintings event.
     *
     * @param idcard the card identifier
     * @param era the card era
     * @param upperNumArtists threshold of ARTIST characters to gain the bonus
     * @param decrPrestigePoints prestige points (negative) if below threshold
     * @param incrPrestigePoints prestige points gained per ARTIST if above threshold
     */
    public CavePaintings(int idcard, int era, int upperNumArtists,
                         int decrPrestigePoints, int incrPrestigePoints) {
        super(idcard, era);
        this.upperNumArtists = upperNumArtists;
        this.decrPrestigePoints = decrPrestigePoints;
        this.incrPrestigePoints = incrPrestigePoints;
    }

    /**
     * Executes the event on all players:
     * applies the prestige change based on ARTIST characters
     * and notifies all cards in the player's tribe.
     *
     * @param players list of involved players
     */
    @Override
    public List<GameEvent> execute(List<Player> players) {
        List<GameEvent> events = new ArrayList<>();
        for (Player player : players) {
            int artistNumber = player.countCharactersOfType(CharacterType.ARTIST);
            String reason = "";

            if (artistNumber < upperNumArtists) {
                player.addPrestige(decrPrestigePoints);
                reason = "Cave paintings: " + artistNumber + " Artists (below the threshold). " + decrPrestigePoints + " PP";
            } else {
                int earned = incrPrestigePoints * artistNumber;
                player.addPrestige(earned);
                reason = "Cave paintings: " + artistNumber + " Artists. +" + earned + " PP";
            }

            events.add(new GameEvent.PlayerResourcesChangedEvent(
                    player.getNickname(),
                    player.getFood(),
                    player.getPrestigePoints(),
                    player.getFoodDiscount(),
                    player.getSustenanceDiscount(),
                    reason
            ));

            for (Card card : player.getTribe()) {
                card.onCavePaintingsEvent(player);
            }
        }
        return events;
    }
}