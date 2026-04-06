package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import it.polimi.ingsw.model.cards.drawableCards.characters.Builder;

import java.util.List;

public class BuilderMastery extends Building {
    public BuilderMastery() {
        this.foodCost= 0; // TODO: check price
        this.prestigePoints = 0; // TODO: check points
    }

    @Override
    public int getFinalPoints(List<DrawableCard> tribe){
        int finalPoint = 0;
        for (DrawableCard card : tribe){
            if(card instanceof Builder){
                card.doubling(); // doubles prestige points of builders NOTE: needs to be executed before counting Builder points
            }
        }
        return prestigePoints;
    }
}
