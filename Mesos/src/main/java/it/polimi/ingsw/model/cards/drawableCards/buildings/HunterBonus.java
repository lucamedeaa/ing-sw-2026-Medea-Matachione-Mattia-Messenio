package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import java.util.List;

public class HunterBonus extends Building {
    public HunterBonus() {
        this.foodCost= 0; // TODO: check price
        this.prestigePoints = 0; // TODO: check points
    }
    @Override
    public void onHuntEvent(Player owner) {
        int num = owner.getHunterNumber();
        owner.addFood(num);
        owner.addPrestige(num);
    }
}
