package it.polimi.ingsw.model.cards.drawableCards.buildings.classScorer;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enums.CharacterType;

public class ClassScorerShaman extends ClassScorer{
        public ClassScorerShaman(int foodCost, int prestigePoints, int era, CharacterType characters){
        super(foodCost, prestigePoints, era, characters);
    }
    @Override
    public int getFinalPoints(Player owner){
        return owner.countCharactersOfType(CharacterType.SHAMAN) * 4 + this.prestigePoints;
    }
}
