package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.enums.CharacterType;

public abstract class Building extends Card {
    protected int foodCost;
    public Building(int idcard, int foodCost, int prestigePoints, int era) {
        super(idcard, era);
        this.foodCost= foodCost;
        this.prestigePoints = prestigePoints;

    }
    protected int prestigePoints;
    public int getFinalPoints(Player owner){return prestigePoints;}
    public void onCardAddedToTribe(Player owner, Card newcard){}
    @Override
    public boolean isPersistent() {
        return true;
    }
    public void placeDuringSetupBottom(Board board){ } //always in the top row by default
    @Override
    public CharacterType getCharacter() {
        return CharacterType.NONCHARACTER;
    }
}
