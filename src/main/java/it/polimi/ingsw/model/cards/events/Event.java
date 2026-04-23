package it.polimi.ingsw.model.cards.events;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.enums.CharacterType;

import java.util.List;

/**
 * Abstract class representing an Event card.
 * <p>
 * Events:
 * <ul>
 *     <li>Are placed in the top row during setup.</li>
 *     <li>Are not persistent.</li>
 *     <li>Cannot be picked by players.</li>
 *     <li>Do not represent a character.</li>
 * </ul>
 */
public abstract class Event extends Card {

    /**
     * Constructs an Event card.
     * @param idcard the card identifier
     * @param era the card era
     */
    public Event(int idcard, int era){
        super(idcard, era);
    }

    /**
     * Places the event in the top row of the board during setup.
     * @param board the game board
     */
    @Override
    public void placeDuringSetupBottom(Board board) {
        board.addTopRow(this);
    }

    /**
     * Events are not persistent.
     * @return always false
     */
    @Override
    public boolean isPersistent() {
        return false;
    }

    /**
     * Events cannot be picked by players.
     * @return always false
     */
    @Override
    public boolean isPickable(){
        return false;
    }

    /**
     * Events do not represent a character.
     * @return CharacterType.NONCHARACTER
     */
    @Override
    public CharacterType getCharacter(){
        return CharacterType.NONCHARACTER;
    }
}