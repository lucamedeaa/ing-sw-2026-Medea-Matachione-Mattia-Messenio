package it.polimi.ingsw.model.updates;

import java.util.List;

/**
 * Interfaccia base che rappresenta le mosse legali calcolate dal Model.
 */
public interface AvailableAction {

    record PlaceTotemAction(List<Integer> availableTileIndices) implements AvailableAction {}
    
    record TakeCardAction(int upperRowPick, int lowerRowPick) implements AvailableAction {}
    
    record SkipAction() implements AvailableAction {}
    
}
