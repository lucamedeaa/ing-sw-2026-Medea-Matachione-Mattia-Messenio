package it.polimi.ingsw.network.dto.actions;

import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.visitor.ActionVisitor;

public record TakeCardActionDTO() implements AvailableActionDTO {
    public void accept(ActionVisitor visitor) {
        visitor.visit(this);
    }
}
