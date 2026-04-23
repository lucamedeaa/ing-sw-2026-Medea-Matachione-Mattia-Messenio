package it.polimi.ingsw.model.cards.drawableCards.buildings;

import it.polimi.ingsw.model.Player;

/** Building that grants a fixed additional amount of prestige points at the end of the game. */
public class VictoryPoints extends Building {

    /** Constructs the VictoryPoints building. @param idcard the card identifier @param foodCost food cost to acquire @param prestigePoints base prestige points @param era the card era */
    public VictoryPoints(int idcard, int foodCost, int prestigePoints, int era) {
        super(idcard, foodCost, prestigePoints, era);
    }

    /** Returns final points including a fixed bonus. @param owner the owning player @return total points */
    @Override
    public int getFinalPoints(Player owner){
        return prestigePoints + 25;
    }
}