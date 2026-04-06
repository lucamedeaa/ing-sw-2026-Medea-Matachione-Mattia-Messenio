package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import java.util.List;

public class ClassScorer extends Building{
    String CharType;
    public ClassScorer(String CharType) {
        this.foodCost= 0; // TODO: check price
        this.prestigePoints = 0; // TODO: check points
        this.CharType = CharType;
    }

    @Override
    public int getFinalPoints(List<DrawableCard> tribe){
        int tot=0;
        switch (CharType){
            case "Hunter":
                for(DrawableCard card:tribe){
                    tot += getHunterNumber();
                }
            case "Collector": // TODO: all different types
                for(DrawableCard card:tribe){
                    tot += getCollectorNumber();
                }
        }
        return tot*3; // x3 multiplier
    }
}
