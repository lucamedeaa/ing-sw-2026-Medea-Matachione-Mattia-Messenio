package it.polimi.ingsw.server.model.card.building.foodDiscount;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.enums.CharacterType;

/** Building that grants a food discount during Sustenance based on the number of INVENTOR characters owned. */
public class FoodDiscountInventor extends FoodDiscount {

    /** Constructs the FoodDiscountInventor building. @param foodCost food cost to acquire @param prestigePoints base prestige points @param era the card era @param characters character type (unused, fixed to INVENTOR logic) */
    public FoodDiscountInventor(int idcard, int foodCost, int prestigePoints, int era, CharacterType characters){
        super(idcard, foodCost, prestigePoints, era, characters);
    }

    /** Provides additional food discount during Sustenance. @param owner the owning player @return discount equal to INVENTOR count */
    @Override
    public int onSustenanceEvent(Player owner){
        return owner.countCharactersOfType(CharacterType.INVENTOR);
    }
}