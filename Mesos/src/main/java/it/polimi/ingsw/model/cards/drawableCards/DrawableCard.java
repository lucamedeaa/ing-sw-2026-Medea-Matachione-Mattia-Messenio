package it.polimi.ingsw.model.cards.drawableCards;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.enums.CharacterType;
import it.polimi.ingsw.model.enums.InventorIcon;

import java.util.Set;

public abstract class DrawableCard extends Card {
    protected int foodCost;
    public DrawableCard() {};
    public int getStarsNumber(){return 0;}
    public int getInventorIconsNumber(Set<InventorIcon> inventorIcons){return 0;}

    public int getFinalPoints(Player owner){return 0;}
    public void onCardAddedToTribe(Player owner, DrawableCard newcard){}

    public void onCavePaintingsEvent(Player owner) {}
    public int onSustenanceEvent(Player owner) {return 0;}
    public void onHuntEvent(Player owner) {}
    public int onShamanicRitualEvent(Player owner, int increment, int decrement) {return 0;}
    public abstract CharacterType getCharacter();

}
