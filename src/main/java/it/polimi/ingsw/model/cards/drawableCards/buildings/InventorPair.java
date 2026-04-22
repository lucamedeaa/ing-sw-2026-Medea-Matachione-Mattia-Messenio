package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.drawableCards.characters.Inventor;
import it.polimi.ingsw.model.enums.CharacterType;
import it.polimi.ingsw.model.enums.InventorIcon;

import java.util.EnumMap;

public class InventorPair extends Building {

    private boolean init;
    private EnumMap<InventorIcon, Integer> iconCount;

    public InventorPair(int idcard, int foodCost, int prestigePoints, int era) {
        super(idcard, foodCost, prestigePoints, era);
        init = false;
        iconCount = new EnumMap<>(InventorIcon.class);
        for (InventorIcon icon : InventorIcon.values()) {
            iconCount.put(icon, 0);
        }
    }

    @Override
    public void onCardAddedToTribe(Player owner, Card newcard){
        InventorIcon icon;
        if(!init){
            for(Card card : owner.getTribe()){
                if(card.getCharacter().equals(CharacterType.INVENTOR)){
                    icon = ((Inventor) card).getInventorIcon();
                    iconCount.put(icon, (iconCount.get(icon) + 1) % 2); //mod 2, in order to ignore previous pairs
                }
            }
            init = true;
            return; //newcard already taken in consideration
        }
        if(newcard.getCharacter().equals(CharacterType.INVENTOR)){
            icon = ((Inventor)newcard).getInventorIcon();
            iconCount.put(icon, (iconCount.get(icon) + 1) % 2);

            if(iconCount.get(icon) == 0){
                owner.addFood(3); //if pair made then food gets added
            }
        }

    }
}
