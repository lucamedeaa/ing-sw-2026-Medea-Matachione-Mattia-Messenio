package it.polimi.ingsw.network.dto.actions;

import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.visitor.ActionVisitor;

import java.util.List;

/** Action DTO representing the ability to place a totem. */
public record PlaceTotemActionDTO(List<Integer> availableTileIndices) implements AvailableActionDTO {

    /** Accepts a visitor. @param visitor */
    @Override
    public void accept(ActionVisitor visitor) {
        visitor.visit(this);
    }


}