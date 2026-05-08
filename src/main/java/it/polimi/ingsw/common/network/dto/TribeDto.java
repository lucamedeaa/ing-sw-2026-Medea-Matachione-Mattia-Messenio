package it.polimi.ingsw.common.network.dto;

import it.polimi.ingsw.common.visitor.EventVisitor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/** Data transfer object representing a player's tribe as a list of card identifiers. */
public record TribeDto(String nickname, List<Integer> tribe) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Accepts a visitor to process this DTO. @param visitor handling the tribe data */
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}
