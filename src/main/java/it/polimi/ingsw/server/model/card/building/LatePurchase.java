package it.polimi.ingsw.server.model.card.building;

/** Building that grants an additional pick from the upper row during the bonus phase. */
public class LatePurchase extends Building {

    public LatePurchase(int idcard) {
        super(idcard);
    }

    /** Provides one additional upper-row pick during the bonus phase. @return bonus picks (1) */
    @Override
    public int getTopRowBonus(){
        return 1;
    }
}