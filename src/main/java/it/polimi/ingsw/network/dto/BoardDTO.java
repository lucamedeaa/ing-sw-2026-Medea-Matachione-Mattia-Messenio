package it.polimi.ingsw.network.dto;

import it.polimi.ingsw.network.visitor.ActionVisitor;

import java.io.Serializable;
import java.util.List;

/** Data transfer object representing the board state, including visible cards, current era, and round. */
public record BoardDTO(List<Integer> UpperRowCards,
                       List<Integer> LowerRowCards,
                       int currentEra,
                       int currentRound) implements Serializable {

    /** Accepts a visitor to process this DTO. @param visitor handling the board data */
    public void accept(ActionVisitor visitor) {
        visitor.visit(this);
    }
}