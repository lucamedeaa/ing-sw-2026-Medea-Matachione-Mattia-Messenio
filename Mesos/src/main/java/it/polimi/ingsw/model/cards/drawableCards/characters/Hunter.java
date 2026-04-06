package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

public class Hunter extends DrawableCard {
    private final boolean hasIcon;

    public Hunter(Boolean hasIcon) {
        this.hasIcon = hasIcon;
    }
    @Override
    public int getHunterNumber(){return 1;}

    public boolean getHasIcon() {return this.hasIcon;}

    @Override
    public void onCardAddedToTribe(Player owner, DrawableCard newcard){
        if(newcard instanceof Hunter && ((Hunter) newcard).getHasIcon()){
            owner.addFood(owner.getHunterNumber());
        }
    }
}
