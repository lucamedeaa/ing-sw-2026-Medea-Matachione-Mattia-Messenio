package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Board;

public class TurnBonus extends Building{
    public TurnBonus() {
        this.foodCost= 0; // TODO: check price
        this.prestigePoints = 0; // TODO: check points
    }
    @Override
    public void onTurnEnded(Player owner, Board board){}

}
