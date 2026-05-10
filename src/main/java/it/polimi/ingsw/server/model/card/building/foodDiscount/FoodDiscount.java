package it.polimi.ingsw.server.model.card.building.foodDiscount;

import it.polimi.ingsw.server.model.card.building.Building;
import it.polimi.ingsw.server.model.enums.CharacterType;

/** Abstract building that provides food discounts based on a specific character type. */
public abstract class FoodDiscount extends Building {

    /** Constructs a FoodDiscount building. @param idcard the card identifier @param foodCost food cost to acquire @param prestigePoints base prestige points @param era the card era @param characters character type used for discount logic */
    public FoodDiscount(int idcard, int foodCost, int prestigePoints, int era, CharacterType characters) {
        super(idcard, foodCost, prestigePoints, era);
    }
}