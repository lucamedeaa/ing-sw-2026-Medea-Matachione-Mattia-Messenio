package it.polimi.ingsw.server.model.card.building.scorer;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.enums.CharacterType;

/** Building that grants end-game points based on the number of ARTIST characters owned. */
public class ClassScorerArtist extends ClassScorer {

    /** Constructs the ClassScorerArtist building. @param foodCost food cost to acquire @param prestigePoints base prestige points @param era the card era @param characters character type (unused, fixed to ARTIST logic) */
    public ClassScorerArtist(int idcard, int foodCost, int prestigePoints, int era, CharacterType characters){
        super(idcard, foodCost, prestigePoints, era, characters);
    }

    /** Computes final points based on ARTIST count. @param owner the owning player @return total points */
    @Override
    public int getFinalPoints(Player owner){
        return owner.countCharactersOfType(CharacterType.ARTIST) * 4 + this.prestigePoints;
    }
}