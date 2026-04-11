package it.polimi.ingsw.model.cards.drawableCards.buildings.foodDiscount;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.buildings.Building;
import it.polimi.ingsw.model.enums.CharacterType;

public abstract class FoodDiscount extends Building {
    private CharacterType CharType;
    public FoodDiscount(int foodCost, int prestigePoints, int era, CharacterType CharType) {
        super(foodCost, prestigePoints, era);
        this.CharType = CharType;
    }
}
