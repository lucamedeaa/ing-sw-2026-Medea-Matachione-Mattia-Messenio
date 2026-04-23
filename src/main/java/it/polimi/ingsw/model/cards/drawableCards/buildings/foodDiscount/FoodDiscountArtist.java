package it.polimi.ingsw.model.cards.drawableCards.buildings.foodDiscount;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enums.CharacterType;

/** Building that grants a food discount during Sustenance based on the number of ARTIST characters owned. */
public class FoodDiscountArtist extends FoodDiscount {

    /** Constructs the FoodDiscountArtist building. @param foodCost food cost to acquire @param prestigePoints base prestige points @param era the card era @param characters character type (unused, fixed to ARTIST logic) */
    public FoodDiscountArtist(int idcard, int foodCost, int prestigePoints, int era, CharacterType characters){
        super(idcard, foodCost, prestigePoints, era, characters);
    }

    /** Provides additional food discount during Sustenance. @param owner the owning player @return discount equal to ARTIST count */
    @Override
    public int onSustenanceEvent(Player owner){
        return owner.countCharactersOfType(CharacterType.ARTIST);
    }
}