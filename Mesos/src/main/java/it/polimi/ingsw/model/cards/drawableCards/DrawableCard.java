package it.polimi.ingsw.model.cards.drawableCards;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.enums.InventorIcon;
import java.util.List;
import java.util.Set;

public abstract class DrawableCard extends Card {
    public DrawableCard() {};
    public int getCollectorNumber(){return 0;}
    public int getHunterNumber(){return 0;}
    public int getStarsNumber(){return 0;}
    public int getBuilderNumber(){return 0;}
    public int getArtistNumber(){return 0;}
    public int getInventorsNumber(){return 0;}
    public int getInventorIconsNumber(Set<InventorIcon> inventorIcons){return 0;}
    public int getFinalPoints(List<DrawableCard> tribe){return 0;}
    public void onCardAddedToTribe(List<DrawableCard> tribe){}
    public void eventPerks(List<DrawableCard> tribe){}

}
