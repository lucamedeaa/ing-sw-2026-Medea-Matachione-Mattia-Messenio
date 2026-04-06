package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import it.polimi.ingsw.model.cards.drawableCards.characters.Inventor;
import it.polimi.ingsw.model.enums.InventorIcon;

import java.util.EnumMap;

public class InventorPair extends Building {

    private boolean init;
    private EnumMap<InventorIcon, Integer> iconCount;

    public InventorPair() {
        this.foodCost= 0; // TODO: check price
        this.prestigePoints = 0; // TODO: check points
        init = false;
        iconCount = new EnumMap<>(InventorIcon.class);
        for (InventorIcon icon : InventorIcon.values()) {
            iconCount.put(icon, 0);
        }
    }

    @Override
    public void onCardAddedToTribe(Player owner, DrawableCard newcard){
        InventorIcon icon;
        if(!init){
            for(DrawableCard Card : owner.getTribe()){
                if(Card instanceof Inventor){
                    icon = ((Inventor) Card).getInventorIcon();
                    iconCount.put(icon, (iconCount.get(icon) + 1) % 2); //mod 2, in order to ignore previous pairs
                }
            }
            init = true;
            return; //newcard already taken in consideration
        }
        if(newcard instanceof Inventor){
            icon = ((Inventor)newcard).getInventorIcon();
            iconCount.put(icon, (iconCount.get(icon) + 1) % 2);

            if(iconCount.get(icon) == 0){
                owner.addFood(3); //if pair made then food gets added
            }
        }

    }
}
