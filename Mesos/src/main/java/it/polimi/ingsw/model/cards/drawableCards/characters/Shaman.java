package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.cards.drawableCards.characters.Character;

public class Shaman extends Character {
    private int starsCount;
    public Shaman(int era, int starsCount){}
    @Override
    public int getStarsNumber(){return starsCount;}
}
