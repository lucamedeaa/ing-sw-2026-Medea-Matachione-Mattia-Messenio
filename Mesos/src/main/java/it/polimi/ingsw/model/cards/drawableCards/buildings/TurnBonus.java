package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Board;

public class TurnBonus extends Building{
    public TurnBonus(int foodCost, int prestigePoints, int era) {
        this.foodCost= foodCost;
        this.prestigePoints = prestigePoints;
        this.era=era;
    }
    @Override
    public void onTurnEnded(Player owner, Board board){
        //TODO: in board
    }
}
