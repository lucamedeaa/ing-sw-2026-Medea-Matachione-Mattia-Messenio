package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import it.polimi.ingsw.model.cards.drawableCards.characters.*;
import it.polimi.ingsw.model.cards.drawableCards.characters.Character;
import it.polimi.ingsw.model.enums.CharacterType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.model.enums.CharacterType.*;

public class DiverseSet extends Building{
    private boolean init;
    private static final Map<CharacterType, Integer> counter = new EnumMap<>(CharacterType.class); //type of map that uses class type as key

    public DiverseSet(int foodCost, int prestigePoints, int era) {
        super(foodCost, prestigePoints, era);
        init=false;
        for (CharacterType c : CharacterType.values()) {  // inizializza tutti gli enum a 0
            counter.put(c, 0);
        }
    }

    @Override
    public void onCardAddedToTribe(Player owner, DrawableCard newcard) {
        boolean setCompleted;
        if (!init) {
            for (DrawableCard card : owner.getTribe()) {
                if (card.countsForDiverseSet()) {
                    counter.put(card.getCharacter(), counter.get(card.getCharacter()) + 1);
                }
            }
            init = true;
            setCompleted = counter.values().stream().allMatch(count -> count > 0);
            //if all the card classes have at least count=1 -> setCompleted
            while (setCompleted) { //ignoring previous completed sets
                for (CharacterType cardCharacter : counter.keySet()) {
                    counter.put(cardCharacter, counter.get(cardCharacter) - 1);
                }
                setCompleted = counter.values().stream().allMatch(count -> count > 0);
            }
            return; //newcard already taken in consideration
        }
        if (!newcard.countsForDiverseSet()) return;

        counter.put(newcard.getCharacter(), counter.get(newcard.getCharacter()) + 1);
        setCompleted = counter.values().stream().allMatch(count -> count > 0);

        if (setCompleted) {
            owner.addFood(5);
            for (CharacterType cardCharacter : counter.keySet()) {
                counter.put(cardCharacter, counter.get(cardCharacter) - 1);
            }
        }
    }
}