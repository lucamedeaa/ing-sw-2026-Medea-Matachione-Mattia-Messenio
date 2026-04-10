package it.polimi.ingsw.model.cards.drawableCards.characters;
import it.polimi.ingsw.model.cards.drawableCards.characters.Character;
import it.polimi.ingsw.model.enums.InventorIcon;

import java.util.Set;

public class Inventor extends Character {
    private InventorIcon inventorIcon;
    public Inventor(int era, InventorIcon inventorIcon){}
    @Override
    public int getInventorsNumber(){return 1;}
    @Override
    public int getInventorIconsNumber(Set<InventorIcon> inventorIcons){return 0;}
}
