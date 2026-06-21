package it.polimi.ingsw.server.model.card.building;

import it.polimi.ingsw.server.model.Player;

/** Building that contributes additional stars during the Shamanic Ritual event. */
public class RitualStars extends Building {

    /**
     * Constructs the RitualStars building.
     * @param idcard the card identifier
     */
    public RitualStars(int idcard) {
        super(idcard);
    }

    /** Adds a fixed contribution to the ritual score. @param owner the owning player @param increment applied prestige increase @param decrement applied prestige decrease @return additional stars (3) */
    @Override
    public int onShamanicRitualEvent(Player owner, int increment, int decrement) {
        return 3;
    }
}