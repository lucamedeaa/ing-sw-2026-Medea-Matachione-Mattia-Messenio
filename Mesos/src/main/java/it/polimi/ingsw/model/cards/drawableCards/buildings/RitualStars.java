package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import java.util.List;

public class RitualStars extends Building {
    public RitualStars(int foodCost, int prestigePoints, int era) {
        this.foodCost= foodCost;
        this.prestigePoints = prestigePoints;
        this.era=era;
    }
    @Override
    public int onShamanicRitualEvent(Player owner, int increment, int decrement) {
        return 3;
    }
}
