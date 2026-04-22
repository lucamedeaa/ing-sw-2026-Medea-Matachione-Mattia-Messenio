package it.polimi.ingsw.model.cards.events;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import java.util.List;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.enums.CharacterType;

public abstract class Event extends Card{
    public Event(int idcard, int era){
        super(idcard,era);
    }
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

    @Override
    public CharacterType getCharacter(){
        return CharacterType.NONCHARACTER;
    }
}

