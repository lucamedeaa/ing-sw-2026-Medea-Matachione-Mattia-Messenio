package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import java.util.List;

public class ClassScorer extends Building{
    private final String CharType;
    public ClassScorer(int foodCost, int prestigePoints, int era, String CharType) {
        this.foodCost= foodCost;
        this.prestigePoints = prestigePoints;
        this.era=era;
        this.CharType = CharType;
    }

    @Override
    public int getFinalPoints(Player owner){
        int tot=0;
        switch (CharType){
            case "Hunter":
                tot = owner.getHunterNumber();
            case "Collector": // TODO: all different types
                tot =  owner.getCollectorNumber();
        }
        return tot*3 + prestigePoints;
    }
}
