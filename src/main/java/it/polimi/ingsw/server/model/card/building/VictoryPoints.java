package it.polimi.ingsw.server.model.card.building;

import it.polimi.ingsw.server.model.Player;

/** Building that grants a fixed additional amount of prestige points at the end of the game. */
public class VictoryPoints extends Building {

    /** Constructs the VictoryPoints building. @param idcard the card identifier @param foodCost food cost to acquire @param prestigePoints base prestige points @param era the card era */
    public VictoryPoints(int idcard) {
        super(idcard);
    }

    /** Returns final points including a fixed bonus. @param owner the owning player @return total points */
    @Override
    public int getFinalPoints(Player owner){
        return prestigePoints;
    }
}