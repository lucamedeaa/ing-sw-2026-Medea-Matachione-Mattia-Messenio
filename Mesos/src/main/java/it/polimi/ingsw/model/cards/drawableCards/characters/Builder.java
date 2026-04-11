package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.enums.CharacterType;

public class Builder extends Character {
    private final int foodDiscount;
    private final int endGamePrestigePoints;
    public Builder(int era, int foodDiscount, int endGamePrestigePoints) {
        this.foodCost=0;
        this.era=era;
        this.foodDiscount = foodDiscount;
        this.endGamePrestigePoints = endGamePrestigePoints;
    }
    @Override
    public int getFoodDiscount(){return foodDiscount;}
    public int getFinalPoints(){return endGamePrestigePoints;}

        @Override
    public CharacterType getCharacter() {
        return CharacterType.BUILDER;
    }
}