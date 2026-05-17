package it.polimi.ingsw.client.view.gui.interaction;

import java.util.List;
import java.util.Set;

public interface BoardView {
    void enableCardSelection(boolean upper, boolean lower, Set<Integer> affordableIds, Set<Integer> unaffordableIds);
    void highlightTotemPlacement(List<Integer> validIndices, boolean iAmOnOffer);
    void disableAllInteractions();
}
