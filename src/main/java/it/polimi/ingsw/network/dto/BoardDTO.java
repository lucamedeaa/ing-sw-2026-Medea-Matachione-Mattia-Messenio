package it.polimi.ingsw.network.dto;

import it.polimi.ingsw.network.visitor.ActionVisitor;
import it.polimi.ingsw.network.visitor.EventVisitor;

import java.io.Serializable;
import java.util.List;

public record BoardDTO(List<String> UpperRowCards,
                       List<String> LowerRowCards,
                       int currentEra,
                       int currentRound) implements Serializable {

    public void accept(ActionVisitor visitor) {
        visitor.visit(this);
    }
}
