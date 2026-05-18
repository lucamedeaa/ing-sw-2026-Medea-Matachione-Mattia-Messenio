package it.polimi.ingsw.common.network.dto;

import it.polimi.ingsw.common.visitor.ActionVisitor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/** Data transfer object representing the board state, including visible cards, current era, and round. */
public record BoardDto(List<Integer> UpperRowCards,
                       List<Integer> LowerRowCards,
                       int currentEra,
                       int currentRound,
                       Integer nextDeckEra) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

}
