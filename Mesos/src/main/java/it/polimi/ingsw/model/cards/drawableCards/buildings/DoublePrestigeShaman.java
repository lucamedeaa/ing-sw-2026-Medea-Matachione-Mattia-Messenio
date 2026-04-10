package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import java.util.List;

public class DoublePrestigeShaman extends Building {
    public DoublePrestigeShaman(int foodCost, int prestigePoints, int era) {
        this.foodCost= foodCost;
        this.prestigePoints = prestigePoints;
        this.era=era;
    }
    @Override
    public int onShamanicRitualEvent(Player owner, int increment, int decrement) {
        if(increment != 0){
            owner.addPrestige(increment); //doubles the increment
        }
        return 0;
    }
}
