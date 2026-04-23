package it.polimi.ingsw.network.dto.actions;

import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.visitor.ActionVisitor;

/** Action DTO representing the ability to place a totem. */
public record PlaceTotemActionDTO() implements AvailableActionDTO {

    /** Accepts a visitor. @param visitor */
    @Override
    public void accept(ActionVisitor visitor) {
        visitor.visit(this);
    }
}