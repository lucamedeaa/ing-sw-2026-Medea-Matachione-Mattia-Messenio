package it.polimi.ingsw.client.view.gui.controllers.board;

import it.polimi.ingsw.client.model.snapshot.PlayerSnapshot;
import it.polimi.ingsw.common.config.CardRegistry;

import java.util.List;

/**
 * Utility policy used by the GUI to decide whether visible cards can be selected.
 */
public final class CardAffordabilityPolicy {
    private CardAffordabilityPolicy() {}

    /**
     * Returns whether a player can afford a card after discounts.
     *
     * @param cardId card identifier
     * @param player player snapshot
     * @return true if the card can be afforded
     */
    public static boolean isAffordable(int cardId, PlayerSnapshot player) {
        Integer baseCost = CardRegistry.getCard(cardId).foodCost();
        if (baseCost == null || baseCost == 0) return true;
        int finalCost = Math.max(baseCost - player.getFoodDiscount(), 0);
        return player.getFood() >= finalCost;
    }

    /**
     * Returns whether a card is an event card.
     *
     * @param cardId card identifier
     * @return true if the card type is Event
     */
    public static boolean isEvent(int cardId) {
        return "Event".equals(CardRegistry.getCard(cardId).type());
    }

}
