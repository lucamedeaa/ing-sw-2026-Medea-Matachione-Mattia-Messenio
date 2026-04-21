package it.polimi.ingsw.model.cards.drawableCards.buildings.foodDiscount;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.buildings.Building;
import it.polimi.ingsw.model.enums.CharacterType;

public abstract class FoodDiscount extends Building {
    private CharacterType charType;
    public FoodDiscount(int foodCost, int prestigePoints, int era, CharacterType characters) {
        super(foodCost, prestigePoints, era);
        this.charType = characters;
    }
}
