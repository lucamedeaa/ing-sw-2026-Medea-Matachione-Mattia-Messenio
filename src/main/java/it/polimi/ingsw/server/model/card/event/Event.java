package it.polimi.ingsw.server.model.card.event;

import it.polimi.ingsw.server.model.card.Card;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.enums.CharacterType;

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
     */
    public Event(int idcard) {
        super(idcard, it.polimi.ingsw.common.config.CardRegistry.getCard(idcard).era());
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