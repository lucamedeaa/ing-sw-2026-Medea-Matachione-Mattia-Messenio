package it.polimi.ingsw.server.model.update;

import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.common.network.dto.action.PlaceTotemActionDto;
import it.polimi.ingsw.common.network.dto.action.TakeCardActionDto;
import it.polimi.ingsw.common.network.dto.action.SkipActionDto;
import java.util.List;

/**
 * Server-side representation of an action currently available to a player.
 */
public interface AvailableAction {

    /**
     * Converts this action to its network DTO.
     *
     * @return action DTO sent to clients
     */
    ActionDto toDTO();

    /**
     * Available action for placing a totem.
     *
     * @param availableTileIndices offer-track indices where placement is legal
     */
    record PlaceTotemAction(List<Integer> availableTileIndices) implements AvailableAction {
        @Override public ActionDto toDTO() { return new PlaceTotemActionDto(availableTileIndices); }
    }

    /**
     * Available action for taking cards from the board.
     *
     * @param upperRowPick remaining upper-row picks
     * @param lowerRowPick remaining lower-row picks
     */
    record TakeCardAction(int upperRowPick, int lowerRowPick) implements AvailableAction {
        @Override public ActionDto toDTO() { return new TakeCardActionDto(upperRowPick, lowerRowPick); }
    }

    /**
     * Available action for skipping an optional action.
     */
    record SkipAction() implements AvailableAction {
        @Override public ActionDto toDTO() { return new SkipActionDto(); }
    }
}
