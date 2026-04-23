package it.polimi.ingsw.model.cards.drawableCards.buildings.foodDiscount;

import it.polimi.ingsw.model.cards.drawableCards.buildings.Building;
import it.polimi.ingsw.model.enums.CharacterType;

/** Abstract building that provides food discounts based on a specific character type. */
public abstract class FoodDiscount extends Building {
    private CharacterType charType;

    /** Constructs a FoodDiscount building. @param foodCost food cost to acquire @param prestigePoints base prestige points @param era the card era @param characters character type used for discount logic */
    public FoodDiscount(int foodCost, int prestigePoints, int era, CharacterType characters) {
        super(foodCost, prestigePoints, era);
        this.charType = characters;
    }
}