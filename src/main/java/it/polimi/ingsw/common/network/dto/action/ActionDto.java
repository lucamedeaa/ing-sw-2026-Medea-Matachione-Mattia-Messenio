package it.polimi.ingsw.common.network.dto.action;

import it.polimi.ingsw.common.visitor.ActionVisitor;

import java.io.Serializable;

/**
 * Marker interface for action DTOs sent to clients.
 */
public interface ActionDto extends Serializable {
    /**
     * Dispatches this action to the given visitor.
     *
     * @param visitor visitor that handles the concrete action type
     */
    void accept(ActionVisitor visitor);
}
