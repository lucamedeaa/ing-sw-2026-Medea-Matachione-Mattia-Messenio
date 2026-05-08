package it.polimi.ingsw.server.model.card.building;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.Card;
import it.polimi.ingsw.server.model.enums.CharacterType;

import java.util.Set;
import java.util.EnumSet;

/** Building that rewards the player with food when completing full sets of all character types. */
public class DiverseSet extends Building {
    private final Set<CharacterType> targetSet;
    private int setsAlreadyRewarded = 0;
    private boolean initialized = false;

    /** Constructs the DiverseSet building. @param idcard the card identifier @param foodCost food cost to acquire @param prestigePoints base prestige points @param era the card era */
    public DiverseSet(int idcard, int foodCost, int prestigePoints, int era) {
        super(idcard, foodCost, prestigePoints, era);
        this.targetSet = EnumSet.allOf(CharacterType.class);
        this.targetSet.remove(CharacterType.NONCHARACTER);
    }

    /** Tracks completed full sets and grants food when new sets are formed after initialization. @param owner the owning player @param newcard the newly added card */
    @Override
    public void onCardAddedToTribe(Player owner, Card newcard) {
        if (!initialized) {
            this.setsAlreadyRewarded = countFullSets(owner);
            this.initialized = true;
            return;
        }
        if (!targetSet.contains(newcard.getCharacter())) return;
        int currentFullSets = countFullSets(owner);
        if (currentFullSets > setsAlreadyRewarded) {
            int newSetsCompleted = currentFullSets - setsAlreadyRewarded;
            owner.addFood(newSetsCompleted * 5);
            this.setsAlreadyRewarded = currentFullSets;
        }
    }

    /** Counts how many complete sets of all character types the player has. @param owner the owning player @return number of complete sets */
    private int countFullSets(Player owner) {
        return (int) targetSet.stream()
                .mapToInt(type -> owner.countCharactersOfType(type))
                .min()
                .orElse(0);
    }
}