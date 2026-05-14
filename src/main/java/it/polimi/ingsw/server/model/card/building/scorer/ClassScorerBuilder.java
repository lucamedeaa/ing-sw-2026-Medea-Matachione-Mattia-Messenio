package it.polimi.ingsw.server.model.card.building.scorer;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.enums.CharacterType;

/** Building that grants end-game points based on the number of BUILDER characters owned. */
public class ClassScorerBuilder extends ClassScorer {

    /** Constructs the ClassScorerBuilder building. @param idcard the card identifier @param foodCost food cost to acquire @param prestigePoints base prestige points @param era the card era @param characters character type (unused, fixed to BUILDER logic) */
    public ClassScorerBuilder(int idcard){
        super(idcard);
    }

    /** Computes final points based on BUILDER count. @param owner the owning player @return total points */
    @Override
    public int getFinalPoints(Player owner){
        return owner.countCharactersOfType(CharacterType.BUILDER) * 4 + this.prestigePoints;
    }
}