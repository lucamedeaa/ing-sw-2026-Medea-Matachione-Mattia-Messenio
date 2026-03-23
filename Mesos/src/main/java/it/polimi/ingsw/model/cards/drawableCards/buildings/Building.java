package it.polimi.ingsw.model.cards.drawableCards.buildings;
import java.util.List;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

public abstract class Building {
    private int foodCost;
    private int prestigePoints;
    public int getFinalPoints(List <DrawableCard> cards){return 0;}
    public void onCardAddedToTribe(Player owner, DrawableCard newCard){}
    public void onTurnEnded(Player owner, Board board){}
    public void eventPerks(Player owner){}


}
