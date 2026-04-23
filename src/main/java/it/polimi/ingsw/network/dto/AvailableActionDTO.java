package it.polimi.ingsw.network.dto;

import it.polimi.ingsw.network.visitor.ActionVisitor;

import java.io.Serializable;

/** Marker interface for DTOs representing available actions, processed via the visitor pattern. */
public interface AvailableActionDTO extends Serializable {

    /** Accepts a visitor to handle the action. @param visitor handling the action */
    void accept(ActionVisitor visitor);
}