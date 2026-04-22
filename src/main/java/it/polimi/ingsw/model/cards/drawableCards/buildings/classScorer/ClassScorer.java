package it.polimi.ingsw.model.cards.drawableCards.buildings.classScorer;
import it.polimi.ingsw.model.cards.drawableCards.buildings.Building;
import it.polimi.ingsw.model.enums.CharacterType;

public abstract class ClassScorer extends Building {
    private CharacterType charType;
    public ClassScorer(int idcard, int foodCost, int prestigePoints, int era, CharacterType characters) {
        super(idcard, foodCost, prestigePoints, era);
        this.charType = characters;
    }
}