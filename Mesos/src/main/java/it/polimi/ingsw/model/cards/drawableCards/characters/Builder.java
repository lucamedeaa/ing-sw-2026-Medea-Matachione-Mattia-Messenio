package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.cards.drawableCards.characters.Character;

public class Builder extends Character {
    private int foodDiscount;
    private int endGamePrestigePoints;
    public Builder(int era, int foodDiscount, int endGamePrestigePoints) {}
    @Override
    public int getBuilderNumber(){return 1;}
}
