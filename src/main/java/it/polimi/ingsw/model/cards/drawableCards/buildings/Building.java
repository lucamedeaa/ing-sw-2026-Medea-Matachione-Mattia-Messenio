package it.polimi.ingsw.model.cards.drawableCards.buildings;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.enums.CharacterType;

/** Abstract base class for building cards. Buildings are persistent, provide prestige points, and do not represent characters. */
public abstract class Building extends Card {
    protected int foodCost;
    protected int prestigePoints;

    /** Constructs a Building card. @param idcard the card identifier @param foodCost food cost to acquire @param prestigePoints base prestige points @param era the card era */
    public Building(int idcard, int foodCost, int prestigePoints, int era) {
        super(idcard, era);
        this.foodCost = foodCost;
        this.prestigePoints = prestigePoints;
    }

    /** Returns the base final points of the building. @param owner the owning player @return prestige points */
    @Override
    public int getFinalPoints(Player owner){
        return prestigePoints;
    }

    /** Hook triggered when a card is added to the tribe. @param owner the owning player @param newcard the newly added card */
    @Override
    public void onCardAddedToTribe(Player owner, Card newcard){}

    /** Buildings are persistent. @return always true */
    @Override
    public boolean isPersistent() {
        return true;
    }

    /** Default placement behavior (typically handled externally for buildings). @param board the game board */
    @Override
    public void placeDuringSetupBottom(Board board){}

    /** Buildings do not represent characters. @return CharacterType.NONCHARACTER */
    @Override
    public CharacterType getCharacter() {
        return CharacterType.NONCHARACTER;
    }
}