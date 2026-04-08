package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

public class Shaman extends DrawableCard {
    private final int starsCount;
    public Shaman(int era, int starsCount){
        this.era=era;
        this.foodCost=0;
        this.starsCount = starsCount;
    }
    @Override
    public int getStarsNumber(){return starsCount;}
}