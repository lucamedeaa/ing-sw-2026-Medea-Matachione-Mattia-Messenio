package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

public class Shaman extends DrawableCard {
    private int starsCount;
    public Shaman(int starsCount){}
    @Override
    public int getStarsNumber(){return starsCount;}
}
