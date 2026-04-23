package it.polimi.ingsw.model.cards.drawableCards.buildings.foodDiscount;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enums.CharacterType;

/** Building that grants a food discount during Sustenance based on the number of COLLECTOR characters owned. */
public class FoodDiscountCollector extends FoodDiscount {

    /** Constructs the FoodDiscountCollector building. @param foodCost food cost to acquire @param prestigePoints base prestige points @param era the card era @param characters character type (unused, fixed to COLLECTOR logic) */
    public FoodDiscountCollector(int foodCost, int prestigePoints, int era, CharacterType characters){
        super(foodCost, prestigePoints, era, characters);
    }

    /** Provides additional food discount during Sustenance. @param owner the owning player @return discount equal to COLLECTOR count */
    @Override
    public int onSustenanceEvent(Player owner){
        return owner.countCharactersOfType(CharacterType.COLLECTOR);
    }
}