package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import java.util.List;

public class HunterBonus extends Building {
    public HunterBonus(int foodCost, int prestigePoints, int era) {
        this.foodCost= foodCost;
        this.prestigePoints = prestigePoints;
        this.era=era;
    }
    @Override
    public void onHuntEvent(Player owner) {
        int num = owner.getHunterNumber();
        owner.addFood(num);
        owner.addPrestige(num);
    }
}
