package it.polimi.ingsw.server.model.card.building.scorer;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.enums.CharacterType;

/** Building that grants end-game points based on the number of SHAMAN characters owned. */
public class ClassScorerShaman extends ClassScorer {

    /** Constructs the ClassScorerShaman building. @param idcard the card identifier */
    public ClassScorerShaman(int idcard){
        super(idcard);
    }

    /** Computes final points based on SHAMAN count. @param owner the owning player @return total points */
    @Override
    public int getFinalPoints(Player owner){
        return owner.countCharactersOfType(CharacterType.SHAMAN) * 4 + this.prestigePoints;
    }
}