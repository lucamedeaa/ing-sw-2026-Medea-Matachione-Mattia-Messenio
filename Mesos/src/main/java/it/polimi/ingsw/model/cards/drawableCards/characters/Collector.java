package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.cards.drawableCards.characters.Character;

public class Collector extends Character {
    public Collector(int era, int discount) {
    private final int discount;
        this.foodCost=0;
        this.era=era;
        this.discount = discount;
    }
    @Override
    public int getCollectorNumber(){return 1;}

    public int getDiscount(){return discount;}
}