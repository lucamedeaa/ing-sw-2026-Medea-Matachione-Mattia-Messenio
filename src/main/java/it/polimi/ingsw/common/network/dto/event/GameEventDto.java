package it.polimi.ingsw.common.network.dto.event;

import it.polimi.ingsw.common.visitor.EventVisitor;

import java.io.Serializable;

/** Marker interface for game event DTOs that can be processed via the visitor pattern. */
public interface GameEventDto extends Serializable {

    /** Accepts a visitor to handle the event. @param visitor handling the event */
    void accept(EventVisitor visitor);
}