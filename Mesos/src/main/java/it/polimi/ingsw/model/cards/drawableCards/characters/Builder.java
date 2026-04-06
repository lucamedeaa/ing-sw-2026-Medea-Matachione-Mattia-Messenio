package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

public class Builder extends DrawableCard {
    private int foodDiscount;
    private int endGamePrestigePoints;
    public Builder(int foodDiscount, int endGamePrestigePoints) {}
    @Override
    public int getBuilderNumber(){return 1;}

    @Override
    public void doubling(){
        this.endGamePrestigePoints += endGamePrestigePoints;
    }
}
