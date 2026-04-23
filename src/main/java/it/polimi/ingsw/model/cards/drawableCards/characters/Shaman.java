package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.enums.CharacterType;

/** Represents a Shaman character card that contributes a fixed number of stars. */
public class Shaman extends Character {
    private final int starsCount;

    /** Constructs a Shaman card. @param idcard the card identifier @param era the card era @param starsCount number of stars provided */
    public Shaman(int idcard, int era, int starsCount){
        super(idcard, era);
        this.foodCost = 0;
        this.starsCount = starsCount;
    }

    /** Returns the number of stars contributed by this card. @return stars count */
    @Override
    public int getStarsNumber(){return starsCount;}

    /** Returns the character type. @return CharacterType.SHAMAN */
    @Override
    public CharacterType getCharacter() {
        return CharacterType.SHAMAN;
    }
}