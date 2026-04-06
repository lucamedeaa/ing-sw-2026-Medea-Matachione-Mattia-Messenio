package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import it.polimi.ingsw.model.cards.drawableCards.characters.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiverseSet extends Building{
    private boolean init;
    private int[] mem;
    private static final Map<Class<?>, Integer> counter = new HashMap<>(); //type of map that uses class type as key

    public DiverseSet() {
        this.foodCost= 0; // TODO: check price
        this.prestigePoints = 0; // TODO: check points
        init=false;
        counter.put(Hunter.class, 0);
        counter.put(Shaman.class, 0);
        counter.put(Artist.class, 0);
        counter.put(Builder.class, 0);
        counter.put(Inventor.class, 0);
        counter.put(Collector.class, 0);
    }

    @Override
    public void onCardAddedToTribe(Player owner, DrawableCard newcard) {
        boolean setCompleted;
        if (!init) {
            for (DrawableCard card : owner.getTribe()) {
                if (card instanceof Building) {
                    continue; //ingnore buildings
                }
                counter.put(card.getClass(), counter.get(card.getClass()) + 1);
            }
            init = true;
            setCompleted = counter.size() == 6 && counter.values().stream().allMatch(count -> count > 0);
            //if all the card classes have at least count=1 -> setCompleted
            while (setCompleted) { //ignoring previous completed sets
                for (Class<?> cardClass : counter.keySet()) {
                    counter.put(cardClass, counter.get(cardClass) - 1);
                }
                setCompleted = counter.size() == 6 && counter.values().stream().allMatch(count -> count > 0);
            }
            return; //newcard already taken in consideration
        }
        if (newcard instanceof Building) {
            return;
        }
        counter.put(newcard.getClass(), counter.get(newcard.getClass()) + 1);
        setCompleted = counter.size() == 6 && counter.values().stream().allMatch(count -> count > 0);

        if (setCompleted) {
            owner.addFood(5);
            for (Class<?> cardClass : counter.keySet()) {
                counter.put(cardClass, counter.get(cardClass) - 1);
            }
        }
    }
}
