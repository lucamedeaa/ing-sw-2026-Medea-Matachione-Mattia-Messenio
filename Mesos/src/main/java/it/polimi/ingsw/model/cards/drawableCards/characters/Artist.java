package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

public class Artist extends DrawableCard {
    public Artist (int era){
       this.foodCost=0;
       this.era=era;
    }
    @Override
    public int getArtistNumber(){return 1;}
}
