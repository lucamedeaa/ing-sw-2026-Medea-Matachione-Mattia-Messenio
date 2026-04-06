package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import java.util.List;

import static java.lang.Math.min;

public class SetScorer extends Building{
    public SetScorer() {
        this.foodCost= 0; // TODO: check price
        this.prestigePoints = 0; // TODO: check points
    }
    @Override
    public int getFinalPoints(Player owner){
        int bonus = min(owner.getHunterNumber(), owner.getArtistNumber(), owner.getShamanNumber(), owner.getCollectorNumber(),
                        owner.getInventorsNumber(), owner.getBuilderNumber()) * 6;


        return prestigePoints + bonus;
    }
}
