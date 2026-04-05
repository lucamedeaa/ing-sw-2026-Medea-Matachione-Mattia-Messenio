package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

import java.util.List;

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

    public void onCardAddedToTribe(Player player, List<DrawableCard> tribe){
        int cnt=1;
        if(this.hasIcon){
            for(DrawableCard drawableCard : tribe){
                if(drawableCard.getHunterNumber() == 1){
                    cnt++;
                }
            }
            player.addFood(cnt);
        }
    }
}
