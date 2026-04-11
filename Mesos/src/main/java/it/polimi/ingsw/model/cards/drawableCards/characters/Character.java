package it.polimi.ingsw.model.cards.drawableCards.characters;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

public abstract class Character extends DrawableCard{
    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public void placeDuringSetupBottom(Board board) {
        board.addBottomRow(this);
    }
}
