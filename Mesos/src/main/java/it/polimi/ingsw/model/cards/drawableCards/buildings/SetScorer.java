package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import java.util.List;

public class SetScorer extends Building{
    public SetScorer(int foodCost, int prestigePoints, int era) {

    }
    @Override
    public int getFinalPoints(List<DrawableCard> cards){return 0;}
}
