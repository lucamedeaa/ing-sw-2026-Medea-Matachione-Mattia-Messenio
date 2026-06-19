package it.polimi.ingsw.server.model.card.building.foodDiscount;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.enums.CharacterType;

/** Building that grants a food discount during Sustenance based on the number of INVENTOR characters owned. */
public class FoodDiscountInventor extends FoodDiscount {

    /** Constructs the FoodDiscountInventor building. @param idcard the card identifier */
    public FoodDiscountInventor(int idcard){
        super(idcard);
    }

    /** Provides additional food discount during Sustenance. @param owner the owning player @return discount equal to INVENTOR count */
    @Override
    public int onSustenanceEvent(Player owner){
        return owner.countCharactersOfType(CharacterType.INVENTOR);
    }
}