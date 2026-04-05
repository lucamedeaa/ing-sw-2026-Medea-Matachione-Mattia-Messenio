package it.polimi.ingsw.model.cards.drawableCards.characters;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import it.polimi.ingsw.model.enums.InventorIcon;

import java.util.Set;

public class Inventor extends DrawableCard {
    private final InventorIcon inventorIcon;

    public Inventor(int era, InventorIcon inventorIcon){
        this.era=era;
        this.foodCost=0;
        this.inventorIcon = inventorIcon;
    }
    @Override
    public int getInventorsNumber(){return 1;}
    @Override
    public int getInventorIconsNumber(Set<InventorIcon> inventorIcons){
        //TODO: Find the purpose of this method :)
        return 0;}

    public InventorIcon getInventorIcon(){return inventorIcon;}
}
