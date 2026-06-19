package it.polimi.ingsw.server.model.card.building;

import it.polimi.ingsw.server.model.Player;

/** Building that doubles the prestige gained during the Shamanic Ritual event. */
public class DoublePrestigeShaman extends Building {

    public DoublePrestigeShaman(int idcard) {
        super(idcard);
    }

    /** Applies additional prestige equal to the increment during Shamanic Ritual, effectively doubling the gain. @param owner the owning player @param increment applied prestige increase @param decrement applied prestige decrease @return always 0 */
    @Override
    public int onShamanicRitualEvent(Player owner, int increment, int decrement) {
        if (increment != 0){
            owner.addPrestige(increment);
        }
        return 0;
    }
}