package it.polimi.ingsw.model.cards.drawableCards.buildings;

/** Building that grants an additional pick from the upper row during the bonus phase. */
public class LatePurchase extends Building {

    /** Constructs the LatePurchase building. @param idcard the card identifier @param foodCost food cost to acquire @param prestigePoints base prestige points @param era the card era */
    public LatePurchase(int idcard, int foodCost, int prestigePoints, int era) {
        super(idcard, foodCost, prestigePoints, era);
    }

    /** Provides one additional upper-row pick during the bonus phase. @return bonus picks (1) */
    @Override
    public int getTopRowBonus(){
        return 1;
    }
}