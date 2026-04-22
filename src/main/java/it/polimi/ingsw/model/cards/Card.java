package it.polimi.ingsw.model.cards;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.enums.CharacterType;
import it.polimi.ingsw.model.enums.InventorIcon;

import java.util.List;
import java.util.Set;

public abstract class Card {
    protected final int era;
    protected final int IDcarta;

    public Card(int idcard, int era){
        this.IDcarta = idcard;
        this.era = era;
    }
    public abstract void placeDuringSetupBottom(Board board);
    public abstract boolean isPersistent();
    public void execute(List<Player> players) {
    }

    public int getResolutionPriority() {
        return 0;
    }
    public boolean isPickable(){
        return true;
    }

    public int getFoodCost(){
        return 0;
    }
    public void addCard(Player player) {};
    public int getEra() {return era;}

    public int getTopRowBonus(){
        return 0;
    }
    public int getFoodBonus() {
        return 0;
    }
    public int getFoodDiscount(){return 0;}
    public void onCardAddedInstantEffects(Player player) {};


    public int getStarsNumber(){return 0;}
    public int getInventorIconsNumber(Set<InventorIcon> inventorIcons){return 0;}

    public int getFinalPoints(Player owner){return 0;}
    public void onCardAddedToTribe(Player owner, Card newcard){}

    public void onCavePaintingsEvent(Player owner) {}
    public int onSustenanceEvent(Player owner) {return 0;}
    public void onHuntEvent(Player owner) {}
    public int onShamanicRitualEvent(Player owner, int increment, int decrement) {return 0;}
    public abstract CharacterType getCharacter();
}