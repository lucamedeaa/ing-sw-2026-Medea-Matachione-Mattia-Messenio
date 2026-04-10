package it.polimi.ingsw.model.cards.drawableCards.buildings;
import java.util.List;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

public abstract class Building extends DrawableCard {
    private int foodCost;
    private int prestigePoints;
    @Override
    public boolean isPersistent() {
        return true;
    }
    public int getFinalPoints(List <DrawableCard> cards){return 0;}
    public void onCardAddedToTribe(List<DrawableCard> tribe){}
    public void onTurnEnded(Player owner, Board board){}
}
