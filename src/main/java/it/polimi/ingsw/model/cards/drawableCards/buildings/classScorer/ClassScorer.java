package it.polimi.ingsw.model.cards.drawableCards.buildings.classScorer;

import it.polimi.ingsw.model.cards.drawableCards.buildings.Building;
import it.polimi.ingsw.model.enums.CharacterType;

/** Abstract building card that scores based on a specific character type. */
public abstract class ClassScorer extends Building {
    private CharacterType charType;

    /** Constructs a ClassScorer building. @param idcard the card identifier @param foodCost food cost to acquire @param prestigePoints base prestige points @param era the card era @param characters character type used for scoring */
    public ClassScorer(int idcard, int foodCost, int prestigePoints, int era, CharacterType characters) {
        super(idcard, foodCost, prestigePoints, era);
        this.charType = characters;
    }
}