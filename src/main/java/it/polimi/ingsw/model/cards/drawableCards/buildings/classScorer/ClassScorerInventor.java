package it.polimi.ingsw.model.cards.drawableCards.buildings.classScorer;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enums.CharacterType;

public class ClassScorerInventor extends ClassScorer{
        public ClassScorerInventor(int foodCost, int prestigePoints, int era, CharacterType characters){
        super(foodCost, prestigePoints, era, characters);
    }
    @Override
    public int getFinalPoints(Player owner){
        return owner.countCharactersOfType(CharacterType.INVENTOR) * 2 + this.prestigePoints;
    }
}
