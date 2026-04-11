package it.polimi.ingsw.model.cards.drawableCards.buildings.classScorer;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enums.CharacterType;

public class ClassScorerBuilder extends ClassScorer {
        public ClassScorerBuilder(int foodCost, int prestigePoints, int era, CharacterType characters){
        super(foodCost, prestigePoints, era, characters);
    }
    @Override
    public int getFinalPoints(Player owner){
        return owner.getBuilderNumber() * 4 + this.prestigePoints;
    }
}
