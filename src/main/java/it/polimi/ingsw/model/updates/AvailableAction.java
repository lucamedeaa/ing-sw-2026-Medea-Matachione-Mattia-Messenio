package it.polimi.ingsw.model.updates;

import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.actions.PlaceTotemActionDTO;
import it.polimi.ingsw.network.dto.actions.TakeCardActionDTO;
import it.polimi.ingsw.network.dto.actions.SkipActionDTO;
import java.util.List;

public interface AvailableAction {

    AvailableActionDTO toDTO();

    record PlaceTotemAction(List<Integer> availableTileIndices) implements AvailableAction {
        @Override public AvailableActionDTO toDTO() { return new PlaceTotemActionDTO(availableTileIndices); }
    }

    record TakeCardAction(int upperRowPick, int lowerRowPick) implements AvailableAction {
        @Override public AvailableActionDTO toDTO() { return new TakeCardActionDTO(upperRowPick, lowerRowPick); }
    }

    record SkipAction() implements AvailableAction {
        @Override public AvailableActionDTO toDTO() { return new SkipActionDTO(); }
    }
}