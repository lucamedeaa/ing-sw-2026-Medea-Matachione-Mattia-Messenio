package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;

public class VictoryPoints extends Building{
    public VictoryPoints(int foodCost, int prestigePoints, int era) {
        super(idcard, foodCost, prestigePoints, era);
    }

    @Override
    public int getFinalPoints(Player owner){return prestigePoints + 25;}
}
