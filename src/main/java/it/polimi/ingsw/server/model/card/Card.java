package it.polimi.ingsw.server.model.card;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.InventorIcon;
import it.polimi.ingsw.server.model.update.GameEvent;

import java.util.List;
import java.util.Set;

/**
 * Base class for all game cards.
 * Defines shared card data and default no-op hooks for card effects.
 */
public abstract class Card {
    protected final int era;
    protected final int IDcard;

    public Card(int idcard, int era) {
        this.IDcard = idcard;
        this.era = era;
    }

    public abstract void placeDuringSetupBottom(Board board);

    public abstract boolean isPersistent();

    public List<GameEvent> execute(List<Player> players) {
        return List.of();
    }

    public int getResolutionPriority() {
        return 0;
    }

    public boolean isPickable() {
        return true;
    }

    public int getFoodCost() {
        return 0;
    }

    public int getEra() {
        return era;
    }

    public int getTopRowBonus() {
        return 0;
    }

    public int getFoodBonus() {
        return 0;
    }

    public int getFoodDiscount() {
        return 0;
    }

    /**
     * Called when the card enters a player's tribe and may apply an immediate effect.
     */
    public void onCardAddedInstantEffects(Player player) {}

    public int getStarsNumber() {
        return 0;
    }

    public int getInventorIconsNumber(Set<InventorIcon> inventorIcons) {
        return 0;
    }

    public int getFinalPoints(Player owner) {
        return 0;
    }

    /**
     * Called after a new card is added to the owner's tribe.
     */
    public void onCardAddedToTribe(Player owner, Card newCard) {}

    /**
     * Called when Cave Paintings is resolved.
     */
    public void onCavePaintingsEvent(Player owner) {}

    /**
     * Called when Sustenance is resolved.
     */
    public int onSustenanceEvent(Player owner) {
        return 0;
    }

    /**
     * Called when Hunt is resolved.
     */
    public void onHuntEvent(Player owner) {}

    /**
     * Called during Shamanic Ritual scoring and prestige application.
     */
    public int onShamanicRitualEvent(Player owner, int increment, int decrement) {
        return 0;
    }

    public abstract CharacterType getCharacter();

    public Integer getIDcard() {
        return IDcard;
    }
}