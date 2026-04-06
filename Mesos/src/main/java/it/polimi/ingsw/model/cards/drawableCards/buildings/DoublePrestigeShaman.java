package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import java.util.List;

public class DoublePrestigeShaman extends Building {
    public DoublePrestigeShaman() {
        this.foodCost= 0; // TODO: check price
        this.prestigePoints = 0; // TODO: check points
    }
    @Override
    public void onShamanicRitualEvent(Player owner) {}
}
