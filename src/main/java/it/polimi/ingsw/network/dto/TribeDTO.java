package it.polimi.ingsw.network.dto;

import it.polimi.ingsw.network.visitor.ClientMessageVisitor;
import it.polimi.ingsw.network.visitor.EventVisitor;

import java.io.Serializable;
import java.util.List;

public record TribeDTO(String nickname, List<Integer> tribe) implements Serializable {
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}
