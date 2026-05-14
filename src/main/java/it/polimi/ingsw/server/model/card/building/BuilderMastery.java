package it.polimi.ingsw.server.model.card.building;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.Card;
import it.polimi.ingsw.server.model.enums.CharacterType;

/** Building that grants additional end-game points by counting BUILDER card points a second time. */
public class BuilderMastery extends Building {

    /** Constructs the BuilderMastery building. @param idcard the card identifier @param foodCost food cost to acquire @param prestigePoints base prestige points @param era the card era */
    public BuilderMastery(int idcard) {
        super(idcard);
    }

    /** Computes final points by summing BUILDER card points again plus base prestige. @param owner the owning player @return total points */
    @Override
    public int getFinalPoints(Player owner){
        int finalPoint = 0;
        for (Card card : owner.getTribe()){
            if (card.getCharacter().equals(CharacterType.BUILDER)){
                finalPoint += card.getFinalPoints(owner);
            }
        }
        return finalPoint + prestigePoints;
    }
}