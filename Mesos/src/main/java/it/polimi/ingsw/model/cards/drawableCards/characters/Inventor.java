package it.polimi.ingsw.model.cards.drawableCards.characters;
import it.polimi.ingsw.model.enums.CharacterType;
import it.polimi.ingsw.model.enums.InventorIcon;

import java.util.Set;

public class Inventor extends Character {
    private final InventorIcon inventorIcon;

    public Inventor(int era, InventorIcon inventorIcon){
        this.era=era;
        this.foodCost=0;
        this.inventorIcon = inventorIcon;
    }

    @Override
    public int getInventorIconsNumber(Set<InventorIcon> inventorIcons){
        //TODO: Find the purpose of this method :)
        return 0;}

    public InventorIcon getInventorIcon(){return inventorIcon;}

    @Override
    public CharacterType getCharacter() {
        return CharacterType.INVENTOR;
    }
}