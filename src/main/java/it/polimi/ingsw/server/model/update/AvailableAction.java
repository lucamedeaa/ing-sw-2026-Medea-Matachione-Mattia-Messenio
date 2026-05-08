package it.polimi.ingsw.server.model.update;

import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.common.network.dto.action.PlaceTotemActionDto;
import it.polimi.ingsw.common.network.dto.action.TakeCardActionDto;
import it.polimi.ingsw.common.network.dto.action.SkipActionDto;
import java.util.List;

public interface AvailableAction {

    ActionDto toDTO();

    record PlaceTotemAction(List<Integer> availableTileIndices) implements AvailableAction {
        @Override public ActionDto toDTO() { return new PlaceTotemActionDto(availableTileIndices); }
    }

    record TakeCardAction(int upperRowPick, int lowerRowPick) implements AvailableAction {
        @Override public ActionDto toDTO() { return new TakeCardActionDto(upperRowPick, lowerRowPick); }
    }

    record SkipAction() implements AvailableAction {
        @Override public ActionDto toDTO() { return new SkipActionDto(); }
    }
}