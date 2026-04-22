package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;

public class DoublePrestigeShaman extends Building {
    public DoublePrestigeShaman(int idcard, int foodCost, int prestigePoints, int era) {
        super(idcard, foodCost, prestigePoints, era);
    }
    @Override
    public int onShamanicRitualEvent(Player owner, int increment, int decrement) {
        if(increment != 0){
            owner.addPrestige(increment); //doubles the increment
        }
        return 0;
    }
}
