package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.cards.drawableCards.characters.Character;

public class Collector extends Character {
    private int discount;
    public Collector(int discount) {
        this.discount = discount;
    }
    @Override
    public int getCollectorNumber(){return 1;}
}
