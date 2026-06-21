package it.polimi.ingsw.server.model.card;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.InventorIcon;
import it.polimi.ingsw.server.model.update.GameEvent;

import java.util.List;
import java.util.Set;

/**
 * Abstract base class representing a generic card.
 * <p>
 * Defines common properties (ID, era) and default behaviors for all cards,
 * including placement, execution, bonuses, and event hooks.
 **/

public abstract class Card {
    protected final int era;
    protected final int IDcard;

    /**
     * Constructs a card.
     * @param idcard the card identifier
     * @param era the card era
     */
    public Card(int idcard, int era) {
        this.IDcard = idcard;
        this.era = era;
    }

    /**
     * Places the card during setup in the bottom area of the board.
     * @param board the game board
     */
    public abstract void placeDuringSetupBottom(Board board);

    /**
     * Indicates whether the card effect is persistent.
     * @return true if persistent, false otherwise
     */
    public abstract boolean isPersistent();

    /**
     * Executes the card effect.
     * @param players list of involved players
     */
    public List<GameEvent> execute(List<Player> players) {
        return List.of();
    }

    /**
     * Defines the resolution priority of the card.
     * @return priority value (default 0)
     */
    public int getResolutionPriority() {
        return 0;
    }

    /**
     * Indicates whether the card can be picked by players.
     * @return true if pickable, false otherwise
     */
    public boolean isPickable() {
        return true;
    }

    /**
     * Returns the food cost of the card.
     * @return food cost (default 0)
     */
    public int getFoodCost() {
        return 0;
    }

    /**
     * Returns the card era.
     * @return era value
     */
    public int getEra() {
        return era;
    }

    /**
     * Returns the bonus when placed in the top row.
     * @return bonus value (default 0)
     */
    public int getTopRowBonus() {
        return 0;
    }

    /**
     * Returns the food bonus provided by the card.
     * @return food bonus (default 0)
     */
    public int getFoodBonus() {
        return 0;
    }

    /**
     * Returns the food discount provided by the card.
     * @return food discount (default 0)
     */
    public int getFoodDiscount() {
        return 0;
    }

    /**
     * Hook triggered when the card is added, for instant effects.
     * @param player the owner
     */
    public void onCardAddedInstantEffects(Player player) {}

    /**
     * Returns the number of stars contributed by the card.
     *
     * @return stars count (default 0)
     */
    public int getStarsNumber() {
        return 0;
    }

    /**
     * Returns the number of inventor icons contributed by the card.
     * @param inventorIcons set of icons already owned
     * @return number of icons (default 0)
     */
    public int getInventorIconsNumber(Set<InventorIcon> inventorIcons) {
        return 0;
    }

    /**
     * Returns final points at the end of the game.
     * @param owner the owning player
     * @return points (default 0)
     */
    public int getFinalPoints(Player owner) {
        return 0;
    }

    /**
     * Hook triggered when a card is added to the tribe.
     * @param owner the owning player
     * @param newCard the newly added card
     */
    public void onCardAddedToTribe(Player owner, Card newCard) {}

    /**
     * Hook triggered during Cave Paintings event.
     * @param owner the owning player
     */
    public void onCavePaintingsEvent(Player owner) {}

    /**
     * Hook triggered during Sustenance event.
     * @param owner the owning player
     * @return additional food discount (default 0)
     */
    public int onSustenanceEvent(Player owner) {
        return 0;
    }

    /**
     * Hook triggered during Hunt event.
     * @param owner the owning player
     */
    public void onHuntEvent(Player owner) {}

    /**
     * Hook triggered during Shamanic Ritual event.
     * @param owner the owning player
     * @param increment applied prestige increase
     * @param decrement applied prestige decrease
     * @return contribution to star count (default 0)
     */
    public int onShamanicRitualEvent(Player owner, int increment, int decrement) {
        return 0;
    }

    /**
     * Returns the character type associated with the card.
     * @return character type
     */
    public abstract CharacterType getCharacter();

    public Integer getIDcard() {
        return IDcard;
    }
}