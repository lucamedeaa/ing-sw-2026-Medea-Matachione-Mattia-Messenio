package it.polimi.ingsw.network.dto;

import it.polimi.ingsw.network.visitor.EventVisitor;

import java.io.Serializable;
import java.util.List;

/** Data transfer object representing a player's tribe as a list of card identifiers. */
public record TribeDTO(String nickname, List<Integer> tribe) implements Serializable {

    /** Accepts a visitor to process this DTO. @param visitor handling the tribe data */
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}