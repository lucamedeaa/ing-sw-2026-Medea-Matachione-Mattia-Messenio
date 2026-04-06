package it.polimi.ingsw.model.cards.drawableCards.characters;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import it.polimi.ingsw.model.enums.InventorIcon;

import java.util.Set;

public class Inventor extends DrawableCard {
    private InventorIcon inventorIcon;
    public Inventor(InventorIcon inventorIcon){}
    @Override
    public int getInventorsNumber(){return 1;}
    @Override
    public int getInventorIconsNumber(Set<InventorIcon> inventorIcons){return 0;}

    public InventorIcon getInventorIcon(){return inventorIcon;}
}
