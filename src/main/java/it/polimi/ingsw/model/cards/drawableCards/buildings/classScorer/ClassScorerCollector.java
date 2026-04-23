package it.polimi.ingsw.model.cards.drawableCards.buildings.classScorer;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enums.CharacterType;

/** Building that grants end-game points based on the number of COLLECTOR characters owned. */
public class ClassScorerCollector extends ClassScorer {

    /** Constructs the ClassScorerCollector building. @param foodCost food cost to acquire @param prestigePoints base prestige points @param era the card era @param characters character type (unused, fixed to COLLECTOR logic) */
    public ClassScorerCollector(int foodCost, int prestigePoints, int era, CharacterType characters){
        super(foodCost, prestigePoints, era, characters);
    }

    /** Computes final points based on COLLECTOR count. @param owner the owning player @return total points */
    @Override
    public int getFinalPoints(Player owner){
        return owner.countCharactersOfType(CharacterType.COLLECTOR) * 4 + this.prestigePoints;
    }
}