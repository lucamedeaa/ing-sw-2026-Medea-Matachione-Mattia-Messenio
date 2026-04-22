package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;

import it.polimi.ingsw.model.enums.CharacterType;

import java.util.EnumSet;
import java.util.Set;

public class SetScorer extends Building {
    private final Set<CharacterType> targetSet;

    public SetScorer(int foodCost, int prestigePoints, int era) {
        super(idcard, foodCost, prestigePoints, era);
        this.targetSet = EnumSet.allOf(CharacterType.class);
        this.targetSet.remove(CharacterType.NONCHARACTER);
    }

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
