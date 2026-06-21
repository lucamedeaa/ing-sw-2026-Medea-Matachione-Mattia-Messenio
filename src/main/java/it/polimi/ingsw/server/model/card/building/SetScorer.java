package it.polimi.ingsw.server.model.card.building;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.enums.CharacterType;

import java.util.EnumSet;
import java.util.Set;

/** Building that grants end-game points based on the number of complete sets of all character types owned. */
public class SetScorer extends Building {
    private final Set<CharacterType> targetSet;

    /**
     * Constructs the SetScorer building.
     * @param idcard the card identifier
     */
    public SetScorer(int idcard) {
        super(idcard);
        this.targetSet = EnumSet.allOf(CharacterType.class);
        this.targetSet.remove(CharacterType.NONCHARACTER);
    }

    /** Computes final points by counting complete sets of all character types. @param owner the owning player @return total points */
    @Override
    public int getFinalPoints(Player owner) {
        long completedSets = targetSet.stream()
                .mapToInt(owner::countCharactersOfType)
                .min()
                .orElse(0);
        int bonus = (int) completedSets * 6;
        return this.prestigePoints + bonus;
    }
}