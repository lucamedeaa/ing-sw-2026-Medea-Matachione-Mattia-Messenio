package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.enums.CharacterType;

/** Represents an Artist character card. Provides no food cost and contributes as an ARTIST type. */
public class Artist extends Character {

    /** Constructs an Artist card. @param idcard the card identifier @param era the card era */
    public Artist (int idcard, int era){
        super(idcard, era);
        this.foodCost = 0;
    }

    /** Returns the character type. @return CharacterType.ARTIST */
    @Override
    public CharacterType getCharacter() {
        return CharacterType.ARTIST;
    }
}