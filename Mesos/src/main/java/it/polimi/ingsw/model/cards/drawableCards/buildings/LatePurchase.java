package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Board;

public class LatePurchase extends Building{
    public LatePurchase(int foodCost, int prestigePoints, int era) {
        super(foodCost, prestigePoints, era);
    }
    @Override
    public int TopRowBonus(){
        return 1;
    }
}
