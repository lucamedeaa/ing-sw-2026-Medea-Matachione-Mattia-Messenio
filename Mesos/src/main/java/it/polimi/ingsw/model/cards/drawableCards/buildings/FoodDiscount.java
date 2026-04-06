package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import java.util.List;

public class FoodDiscount extends Building {
    private final String CharType;
    public FoodDiscount(String CharType) {
        this.foodCost= 0; // TODO: check price
        this.prestigePoints = 0; // TODO: check points
        this.CharType = CharType;
    }
    @Override
    public int onSustenanceEvent(Player owner) {
        int tot = 0;
        switch (CharType){
            case "Artist":
                tot = owner.getArtistNumber();
            case "Collector":
                tot = owner.getCollectorNumber();
            case "Inventor":
                tot = owner.getInventorsNumber();
        }
        return tot;
    }
}
