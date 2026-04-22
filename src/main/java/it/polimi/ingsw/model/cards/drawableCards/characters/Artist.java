package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.enums.CharacterType;

public class Artist extends Character {
    public Artist (int idcard, int era){
        super(idcard, era);
       this.foodCost=0;
    }

    @Override
    public CharacterType getCharacter() {
        return CharacterType.ARTIST;
    }

}