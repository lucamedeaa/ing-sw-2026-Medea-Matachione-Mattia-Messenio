package it.polimi.ingsw.server.model.card.building.foodDiscount;

import it.polimi.ingsw.server.model.card.building.Building;
import it.polimi.ingsw.server.model.enums.CharacterType;

/** Abstract building that provides food discounts based on a specific character type. */
public abstract class FoodDiscount extends Building {

    /** Constructs a FoodDiscount building. @param idcard the card identifier */
    public FoodDiscount(int idcard) {
        super(idcard);
    }
}