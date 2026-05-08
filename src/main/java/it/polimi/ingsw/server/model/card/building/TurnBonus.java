package it.polimi.ingsw.server.model.card.building;

/** Building that grants additional food when returning the totem based on turn order bonuses. */
public class TurnBonus extends Building {

    /** Constructs the TurnBonus building. @param idcard the card identifier @param foodCost food cost to acquire @param prestigePoints base prestige points @param era the card era */
    public TurnBonus(int idcard, int foodCost, int prestigePoints, int era) {
        super(idcard, foodCost, prestigePoints, era);
    }

    /** Provides an additional food bonus when turn-order bonuses are applied. @return food bonus (1) */
    @Override
    public int getFoodBonus() {
        return 1;
    }
}