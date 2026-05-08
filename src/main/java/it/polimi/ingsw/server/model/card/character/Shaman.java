package it.polimi.ingsw.server.model.card.character;

import it.polimi.ingsw.server.model.enums.CharacterType;

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