package it.polimi.ingsw.server.model.card.building;

import it.polimi.ingsw.server.model.Player;

/** Building that prevents prestige loss during the Shamanic Ritual event. */
public class RitualShield extends Building {

    public RitualShield(int idcard) {
        super(idcard);
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