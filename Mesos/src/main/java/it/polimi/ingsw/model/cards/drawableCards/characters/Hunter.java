package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

public class Hunter extends DrawableCard {
    private Boolean hasIcon;
    public Hunter(Boolean hasIcon) {}
    @Override
    public int getHunterNumber(){return 1;}
}
