package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enums.CharacterType;

public class HunterBonus extends Building {
    public HunterBonus(int idcard, int foodCost, int prestigePoints, int era) {
        super(idcard, foodCost, prestigePoints, era);
    }
    @Override
    public void onHuntEvent(Player owner) {
        int num = owner.countCharactersOfType(CharacterType.HUNTER);
        owner.addFood(num);
        owner.addPrestige(num);
    }
}
