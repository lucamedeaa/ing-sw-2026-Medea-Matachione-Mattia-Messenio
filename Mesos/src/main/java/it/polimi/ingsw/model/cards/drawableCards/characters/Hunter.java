package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

public class Hunter extends DrawableCard {
    private final Boolean hasIcon;
    public Hunter(int era,Boolean hasIcon) {
        this.foodCost=0;
        this.era=era;
        this.hasIcon = hasIcon;
    }
    @Override
    public int getHunterNumber(){return 1;}

    public Boolean getHasIcon() {
        return hasIcon;
    }
    @Override
    public void onCardAddedToTribe(Player owner, DrawableCard newcard){
        if(newcard instanceof Hunter && ((Hunter) newcard).getHasIcon()){
            owner.addFood(owner.getHunterNumber());
        }
    }
}
