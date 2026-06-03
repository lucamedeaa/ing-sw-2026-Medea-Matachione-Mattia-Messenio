package it.polimi.ingsw.server.model.card.building.scorer;

import it.polimi.ingsw.server.model.card.building.Building;
import it.polimi.ingsw.server.model.enums.CharacterType;

/** Abstract building card that scores based on a specific character type. */
public abstract class ClassScorer extends Building {

    /** Constructs a ClassScorer building. @param idcard the card identifier @param foodCost food cost to acquire @param prestigePoints base prestige points @param era the card era @param characters character type used for scoring */
    public ClassScorer(int idcard) {
        super(idcard);
    }
}