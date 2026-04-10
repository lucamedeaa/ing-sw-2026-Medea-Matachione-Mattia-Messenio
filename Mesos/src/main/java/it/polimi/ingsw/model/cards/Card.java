package it.polimi.ingsw.model.cards;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Board;
import java.util.List;

public abstract class Card {
    protected int era;
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

    public int TopRowBonus(){
        return 0;
    }
    public int getFoodBonus() {
        return 0;
    }
    public int getFoodDiscount(){return 0;}
}