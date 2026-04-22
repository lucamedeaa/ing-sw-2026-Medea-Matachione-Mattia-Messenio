package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.enums.CharacterType;

public class Collector extends Character {
    private final int discount;
    public Collector(int idcard, int era, int discount) {
        super(idcard, era);
        this.foodCost=0;
        this.discount = discount;
    }

    public int getFoodDiscount(){return discount;}

        @Override
    public CharacterType getCharacter() {
        return CharacterType.COLLECTOR;
    }
}