package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import it.polimi.ingsw.model.cards.drawableCards.characters.Builder;

import java.util.List;

public class BuilderMastery extends Building {
    public BuilderMastery() {
        this.foodCost= 0; // TODO: check price
        this.prestigePoints = 0; // TODO: check points
    }

    @Override
    public int getFinalPoints(Player owner){
        int finalPoint = 0;
        for (DrawableCard card : owner.getTribe()){
            if(card instanceof Builder){
                finalPoint += card.getFinalPoints(owner); // owner useless
            }
        }
        return finalPoint + prestigePoints; //instead of doubling builder points, it counts them here a second time
    }
}
