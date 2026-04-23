package it.polimi.ingsw.model.cards.events;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.enums.CharacterType;

import java.util.List;

/**
 * "Sustenance" event.
 * <p>
 * For each player:
 * <ul>
 *     <li>Computes a food discount based on COLLECTOR characters and card effects.</li>
 *     <li>Determines the total food required to sustain all non-character cards.</li>
 *     <li>If food is insufficient, all food is consumed and prestige is lost.</li>
 *     <li>Otherwise, food is reduced according to the required amount.</li>
 * </ul>
 */
public class Sustenance extends Event {
    private final int numPrestRem;

    /**
     * Constructs the Sustenance event.
     *
     * @param idcard the card identifier
     * @param era the card era
     * @param numPrestRem prestige points lost per missing food unit
     */
    public Sustenance(int idcard, int era, int numPrestRem) {
        super(idcard, era);
        this.numPrestRem = numPrestRem;
    }

    /**
     * Executes the event:
     * computes food requirements and applies food and prestige changes.
     *
     * @param players list of involved players
     */
    @Override
    public void execute(List<Player> players) {
        for (Player player : players) {
            int discount = player.countCharactersOfType(CharacterType.COLLECTOR) * 3;

            for (Card card : player.getTribe()) {
                discount += card.onSustenanceEvent(player);
            }

            int total = 0;
            int playerFood = player.getFood();
            for (Card card : player.getTribe()) {
                if (!(card.getCharacter().equals(CharacterType.NONCHARACTER))) {
                    total += 1;
                }
            }

            if (playerFood + discount < total) {
                player.addFood(-playerFood);
                player.addPrestige(-numPrestRem * (total - (playerFood + discount)));
            } else if (total > discount) {
                player.addFood(-(total - discount));
            }
        }
    }

    /**
     * Defines the resolution priority of the event.
     *
     * @return priority value (1)
     */
    @Override
    public int getResolutionPriority() {
        return 1;
    }
}