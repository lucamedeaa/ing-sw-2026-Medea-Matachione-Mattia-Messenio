package it.polimi.ingsw.network.dto;

import it.polimi.ingsw.network.visitor.EventVisitor;

import java.io.Serializable;

/** Marker interface for game event DTOs that can be processed via the visitor pattern. */
public interface GameEventDTO extends Serializable {

    /** Accepts a visitor to handle the event. @param visitor handling the event */
    void accept(EventVisitor visitor);
}