package it.polimi.ingsw.model.cards.events;
import it.polimi.ingsw.model.Player;
import java.util.List;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.board.Board;

public abstract class Event extends Card{
    @Override
    public void placeDuringSetupBottom(Board board) {
        board.addTopRow(this);
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public boolean isPickable(){
        return false;
    }

}

