package it.polimi.ingsw.model.cards.drawableCards.buildings.foodDiscount;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enums.CharacterType;


public class FoodDiscountCollector extends FoodDiscount {
    public FoodDiscountCollector(int foodCost, int prestigePoints, int era, CharacterType characters){
        super(foodCost, prestigePoints, era, characters);
    }
    @Override
    public int onSustenanceEvent(Player owner){
        return owner.countCharactersOfType(CharacterType.COLLECTOR);
    }
}


