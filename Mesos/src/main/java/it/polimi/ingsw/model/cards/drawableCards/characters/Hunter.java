package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.cards.drawableCards.characters.Character;

public class Hunter extends Character{
    private Boolean hasIcon;
    public Hunter(Boolean hasIcon) {}
    @Override
    public int getHunterNumber(){return 1;}
}
