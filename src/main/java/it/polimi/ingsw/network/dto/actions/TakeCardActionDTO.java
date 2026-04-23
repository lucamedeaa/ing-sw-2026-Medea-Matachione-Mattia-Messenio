package it.polimi.ingsw.network.dto.actions;

import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.visitor.ActionVisitor;

/** Action DTO representing the ability to take a card. */
public record TakeCardActionDTO(int upperRowPick, int lowerRowPick) implements AvailableActionDTO {

    /** Accepts a visitor. @param visitor */
    public void accept(ActionVisitor visitor) {
        visitor.visit(this);
    }
}