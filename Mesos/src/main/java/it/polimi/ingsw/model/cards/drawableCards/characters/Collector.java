package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.enums.CharacterType;

public class Collector extends Character {
    private final int discount;
    public Collector(int era, int discount) {
        this.foodCost=0;
        this.era=era;
        this.discount = discount;
    }
    @Override
    public int getCollectorNumber(){return 1;}

    public int getDiscount(){return discount;}

        @Override
    public CharacterType getCharacter() {
        return CharacterType.COLLECTOR;
    }
}