package it.polimi.ingsw.client.view.gui.controllers.board;

import it.polimi.ingsw.client.model.snapshot.PlayerSnapshot;
import it.polimi.ingsw.common.config.CardRegistry;

import java.util.List;

public final class CardAffordabilityPolicy {
    private CardAffordabilityPolicy() {}

    public static boolean isAffordable(int cardId, PlayerSnapshot player) {
        Integer baseCost = CardRegistry.getCard(cardId).foodCost();
        if (baseCost == null || baseCost == 0) return true;
        int finalCost = Math.max(baseCost - player.getFoodDiscount(), 0);
        return player.getFood() >= finalCost;
    }

    public static boolean isEvent(int cardId) {
        return "Event".equals(CardRegistry.getCard(cardId).type());
    }

}