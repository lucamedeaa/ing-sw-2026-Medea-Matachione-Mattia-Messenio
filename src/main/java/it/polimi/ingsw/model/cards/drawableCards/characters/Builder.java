package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enums.CharacterType;

/** Represents a Builder character card that provides a food discount and end-game prestige points. */
public class Builder extends Character {
    private final int foodDiscount;
    private final int endGamePrestigePoints;

    /** Constructs a Builder card. @param idcard the card identifier @param era the card era @param foodDiscount food discount provided @param endGamePrestigePoints prestige points awarded at game end */
    public Builder(int idcard, int era, int foodDiscount, int endGamePrestigePoints) {
        super(idcard, era);
        this.foodDiscount = foodDiscount;
        this.endGamePrestigePoints = endGamePrestigePoints;
    }

    /** Returns the food discount provided by this card. @return food discount value */
    @Override
    public int getFoodDiscount(){return foodDiscount;}

    /** Returns the end-game prestige points. @param owner the owning player @return prestige points */
    @Override
    public int getFinalPoints(Player owner){return endGamePrestigePoints;}

    /** Returns the character type. @return CharacterType.BUILDER */
    @Override
    public CharacterType getCharacter() {
        return CharacterType.BUILDER;
    }
}