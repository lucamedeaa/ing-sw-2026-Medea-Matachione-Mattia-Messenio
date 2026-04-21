package it.polimi.ingsw.model.cards.drawableCards.buildings.foodDiscount;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enums.CharacterType;

public class FoodDiscountArtist extends FoodDiscount {
    public FoodDiscountArtist(int foodCost, int prestigePoints, int era, CharacterType characters){
        super(foodCost, prestigePoints, era, characters);
    }
    @Override
    public int onSustenanceEvent(Player owner){
        return owner.countCharactersOfType(CharacterType.ARTIST);
    }
}
