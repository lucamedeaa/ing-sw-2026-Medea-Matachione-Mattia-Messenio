package it.polimi.ingsw.common.network.dto.action;

import java.io.Serial;

import it.polimi.ingsw.common.visitor.ActionVisitor;

/**
 * Action DTO representing the ability to take cards from the board.
 *
 * @param upperRowPick remaining picks from the upper row
 * @param lowerRowPick remaining picks from the lower row
 */
public record TakeCardActionDto(int upperRowPick, int lowerRowPick) implements ActionDto {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Accepts a visitor. @param visitor handling this action */
    public void accept(ActionVisitor visitor) {
        visitor.visit(this);
    }


}
