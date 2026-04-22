package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Board;

public class TurnBonus extends Building{
    public TurnBonus(int foodCost, int prestigePoints, int era) {
        super(idcard, foodCost, prestigePoints, era);
    }
    @Override
    public int getFoodBonus() {
        return 1;
    }
}
