package it.polimi.ingsw.model.cards.drawableCards.buildings;

import it.polimi.ingsw.model.Player;

/** Building that prevents prestige loss during the Shamanic Ritual event. */
public class RitualShield extends Building {

    /** Constructs the RitualShield building. @param idcard the card identifier @param foodCost food cost to acquire @param prestigePoints base prestige points @param era the card era */
    public RitualShield(int idcard, int foodCost, int prestigePoints, int era) {
        super(idcard, foodCost, prestigePoints, era);
    }

    /** Cancels any prestige loss applied during Shamanic Ritual. @param owner the owning player @param increment applied prestige increase @param decrement applied prestige decrease @return always 0 */
    @Override
    public int onShamanicRitualEvent(Player owner, int increment, int decrement) {
        if (decrement != 0){
            owner.addPrestige(-decrement);
        }
        return 0;
    }
}