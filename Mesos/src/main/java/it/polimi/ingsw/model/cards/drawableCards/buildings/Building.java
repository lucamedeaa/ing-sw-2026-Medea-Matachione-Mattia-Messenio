package it.polimi.ingsw.model.cards.drawableCards.buildings;
import java.util.List;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

public abstract class Building extends DrawableCard {
    protected int foodCost;
    protected int prestigePoints;
    protected int era;
    public int getFinalPoints(Player owner){return prestigePoints;}
    public void onCardAddedToTribe(Player owner, DrawableCard newcard){}
    @Override
    public boolean isPersistent() {
        return true;
    }
    public void onTurnEnded(Player owner, Board board){}
    public void placeDuringSetupBottom(Board board){ } //always in the top row by default
}
