package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.enums.CharacterType;

public class Artist extends Character {
    public Artist (int era){
       this.foodCost=0;
       this.era=era;
    }
    @Override
    public int getArtistNumber(){return 1;}

    @Override
    public CharacterType getCharacter() {
        return CharacterType.ARTIST;
    }

}