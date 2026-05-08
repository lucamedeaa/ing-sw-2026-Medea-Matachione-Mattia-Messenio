package it.polimi.ingsw.server.model.card.building;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.enums.CharacterType;

/** Building that grants additional food and prestige during the Hunt event based on the number of HUNTER characters owned. */
public class HunterBonus extends Building {

    /** Constructs the HunterBonus building. @param idcard the card identifier @param foodCost food cost to acquire @param prestigePoints base prestige points @param era the card era */
    public HunterBonus(int idcard, int foodCost, int prestigePoints, int era) {
        super(idcard, foodCost, prestigePoints, era);
    }

    /** Grants food and prestige equal to the number of HUNTER characters during the Hunt event. @param owner the owning player */
    @Override
    public void onHuntEvent(Player owner) {
        int num = owner.countCharactersOfType(CharacterType.HUNTER);
        owner.addFood(num);
        owner.addPrestige(num);
    }
}