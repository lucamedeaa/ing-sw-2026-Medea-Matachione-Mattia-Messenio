package it.polimi.ingsw.model.cards.drawableCards.characters;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.Card;

public abstract class Character extends Card {
    protected int foodCost;
    public Character(int idcard, int era) {
        super(idcard, era);
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public void placeDuringSetupBottom(Board board) {
        board.addBottomRow(this);
    }

}
