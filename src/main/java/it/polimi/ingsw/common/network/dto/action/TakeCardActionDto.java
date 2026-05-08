package it.polimi.ingsw.common.network.dto.action;

import java.io.Serial;

import it.polimi.ingsw.common.visitor.ActionVisitor;

/** Action DTO representing the ability to take a card. */
public record TakeCardActionDto(int upperRowPick, int lowerRowPick) implements ActionDto {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Accepts a visitor. @param visitor */
    public void accept(ActionVisitor visitor) {
        visitor.visit(this);
    }


}
