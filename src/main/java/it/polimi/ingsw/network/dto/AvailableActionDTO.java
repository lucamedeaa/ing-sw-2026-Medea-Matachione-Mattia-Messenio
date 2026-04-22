package it.polimi.ingsw.network.dto;

import it.polimi.ingsw.network.visitor.ActionVisitor;

import java.io.Serializable;

public interface AvailableActionDTO extends Serializable {
    void accept(ActionVisitor visitor);
}
