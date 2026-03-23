package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

public class Collector extends DrawableCard {
    private int discount;
    public Collector(int discount) {
        this.discount = discount;
    }
    @Override
    public int getCollectorNumber(){return 1;}
}
