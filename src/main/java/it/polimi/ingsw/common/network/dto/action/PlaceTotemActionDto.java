package it.polimi.ingsw.common.network.dto.action;

import java.io.Serial;

import it.polimi.ingsw.common.visitor.ActionVisitor;

import java.util.List;

/**
 * Action DTO representing the ability to place a totem.
 *
 * @param availableTileIndices offer-track positions where the player may place the totem
 */
public record PlaceTotemActionDto(List<Integer> availableTileIndices) implements ActionDto {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Accepts a visitor. @param visitor handling this action */
    @Override
    public void accept(ActionVisitor visitor) {
        visitor.visit(this);
    }


}
