package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.enums.CharacterType;
import java.util.Set;
import java.util.EnumSet;

public class DiverseSet extends Building {
    private final Set<CharacterType> targetSet;
    private int setsAlreadyRewarded = 0;
    private boolean initialized = false;

    public DiverseSet(int idcard, int foodCost, int prestigePoints, int era) {
        super(idcard, foodCost, prestigePoints, era);
        this.targetSet = EnumSet.allOf(CharacterType.class);
        this.targetSet.remove(CharacterType.NONCHARACTER);
    }

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

    private int countFullSets(Player owner) {
        return (int) targetSet.stream()
                .mapToInt(type -> owner.countCharactersOfType(type))
                .min()
                .orElse(0);
    }
}