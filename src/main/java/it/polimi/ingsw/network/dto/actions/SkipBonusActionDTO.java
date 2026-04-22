package it.polimi.ingsw.network.dto.actions;

import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.visitor.ActionVisitor;
import it.polimi.ingsw.network.visitor.ServerMessageVisitor;

public record SkipBonusActionDTO() implements AvailableActionDTO, ServerMessageVisitor {
        public void accept(ActionVisitor visitor) {
        visitor.visit(this);
    }
}
