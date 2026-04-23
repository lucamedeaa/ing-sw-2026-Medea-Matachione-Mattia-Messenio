package it.polimi.ingsw.model.cards.drawableCards.buildings.classScorer;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enums.CharacterType;

/** Building that grants end-game points based on the number of SHAMAN characters owned. */
public class ClassScorerShaman extends ClassScorer {

    /** Constructs the ClassScorerShaman building. @param foodCost food cost to acquire @param prestigePoints base prestige points @param era the card era @param characters character type (unused, fixed to SHAMAN logic) */
    public ClassScorerShaman(int idcard, int foodCost, int prestigePoints, int era, CharacterType characters){
        super(idcard, foodCost, prestigePoints, era, characters);
    }

    /** Computes final points based on SHAMAN count. @param owner the owning player @return total points */
    @Override
    public int getFinalPoints(Player owner){
        return owner.countCharactersOfType(CharacterType.SHAMAN) * 4 + this.prestigePoints;
    }
}