package it.polimi.ingsw.model.cards.drawableCards.buildings;
import java.util.List;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

public abstract class Building extends DrawableCard {
    protected int foodCost;
    protected int prestigePoints;
    public int getFinalPoints(Player owner){return prestigePoints;}
    public void onCardAddedToTribe(Player owner, DrawableCard newcard){}
    public void onTurnEnded(Player owner, Board board){}
}
