package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import it.polimi.ingsw.model.enums.CharacterType;

public abstract class Building extends DrawableCard {
    public Building(int foodCost, int prestigePoints, int era) {
        this.foodCost= foodCost;
        this.prestigePoints = prestigePoints;
        this.era = era;
    }
    protected int foodCost;
    protected int prestigePoints;
    protected int era;
    public int getFinalPoints(Player owner){return prestigePoints;}
    public void onCardAddedToTribe(Player owner, DrawableCard newcard){}
    @Override
    public boolean isPersistent() {
        return true;
    }
    public void placeDuringSetupBottom(Board board){ } //always in the top row by default
    @Override
    public CharacterType getCharacter() {
        return null;
    }
}
