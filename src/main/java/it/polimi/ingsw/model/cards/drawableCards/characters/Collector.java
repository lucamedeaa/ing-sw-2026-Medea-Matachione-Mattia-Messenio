package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.enums.CharacterType;

/** Represents a Collector character card that provides a food discount. */
public class Collector extends Character {
    private final int discount;

    /** Constructs a Collector card. @param idcard the card identifier @param era the card era @param discount food discount provided */
    public Collector(int idcard, int era, int discount) {
        super(idcard, era);
        this.foodCost = 0;
        this.discount = discount;
    }

    /** Returns the food discount provided by this card. @return discount value */
    @Override
    public int getFoodDiscount(){return discount;}

    /** Returns the character type. @return CharacterType.COLLECTOR */
    @Override
    public CharacterType getCharacter() {
        return CharacterType.COLLECTOR;
    }
}