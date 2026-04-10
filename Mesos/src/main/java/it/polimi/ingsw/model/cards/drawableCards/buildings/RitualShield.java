package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import java.util.List;

public class RitualShield extends Building {
    public RitualShield(int foodCost, int prestigePoints, int era) {
        this.foodCost= foodCost;
        this.prestigePoints = prestigePoints;
        this.era=era;
    }
    @Override
    public int onShamanicRitualEvent(Player owner, int increment, int decrement) {
        if(decrement != 0){
            owner.addPrestige(-decrement); //nullifies the effect of the decrement
        }
        return 0;
    }
}
