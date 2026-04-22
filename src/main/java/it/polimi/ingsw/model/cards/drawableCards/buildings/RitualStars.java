package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;

public class RitualStars extends Building {
    public RitualStars(int foodCost, int prestigePoints, int era) {
        super(idcard, foodCost, prestigePoints, era);
    }
    @Override
    public int onShamanicRitualEvent(Player owner, int increment, int decrement) {
        return 3;
    }
}
