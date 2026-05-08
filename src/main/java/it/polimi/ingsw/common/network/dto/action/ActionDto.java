package it.polimi.ingsw.common.network.dto.action;

import it.polimi.ingsw.common.visitor.ActionVisitor;

import java.io.Serializable;

public interface ActionDto extends Serializable {
    void accept(ActionVisitor visitor);
}