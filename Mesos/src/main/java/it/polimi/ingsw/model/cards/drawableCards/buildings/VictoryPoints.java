package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import java.util.List;

public class VictoryPoints extends Building{
    public VictoryPoints() {
        this.foodCost= 0; // TODO: check price
        this.prestigePoints = 0; // TODO: check points
    }
    @Override
    public int getFinalPoints(List<DrawableCard> tribe){return 0;}
}
