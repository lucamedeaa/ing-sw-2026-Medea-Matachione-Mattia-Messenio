package it.polimi.ingsw.network.dto;

import it.polimi.ingsw.network.visitor.EventVisitor;

import java.io.Serializable;

public interface GameEventDTO extends Serializable {
    void accept(EventVisitor visitor);
}
