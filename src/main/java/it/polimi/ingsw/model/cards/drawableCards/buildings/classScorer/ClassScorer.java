package it.polimi.ingsw.model.cards.drawableCards.buildings.classScorer;
import it.polimi.ingsw.model.cards.drawableCards.buildings.Building;
import it.polimi.ingsw.model.enums.CharacterType;

public abstract class ClassScorer extends Building {
    private CharacterType charType;
    public ClassScorer(int foodCost, int prestigePoints, int era, CharacterType characters) {
        super(foodCost, prestigePoints, era);
        this.charType = characters;
    }
}