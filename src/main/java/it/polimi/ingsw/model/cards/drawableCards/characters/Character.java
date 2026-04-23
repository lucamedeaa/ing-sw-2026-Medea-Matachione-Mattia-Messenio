package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.Card;

/** Abstract base class for character cards. Characters are non-persistent and are placed in the bottom row during setup. */
public abstract class Character extends Card {
    protected int foodCost;

    /** Constructs a Character card. @param idcard the card identifier @param era the card era */
    public Character(int idcard, int era) {
        super(idcard, era);
    }

    /** Characters are not persistent. @return always false */
    @Override
    public boolean isPersistent() {
        return false;
    }

    /** Places the character in the bottom row during setup. @param board the game board */
    @Override
    public void placeDuringSetupBottom(Board board) {
        board.addBottomRow(this);
    }
}