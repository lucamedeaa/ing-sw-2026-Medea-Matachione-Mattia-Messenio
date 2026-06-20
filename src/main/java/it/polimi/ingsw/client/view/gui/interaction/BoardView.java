package it.polimi.ingsw.client.view.gui.interaction;

import java.util.List;
import java.util.Set;

/**
 * View operations required by board interaction logic.
 */
public interface BoardView {
    /**
     * Enables selectable card highlights.
     *
     * @param upper true if upper row cards may be selected
     * @param lower true if lower row cards may be selected
     * @param affordableIds affordable selectable card identifiers
     * @param unaffordableIds unaffordable selectable card identifiers
     */
    void enableCardSelection(boolean upper, boolean lower, Set<Integer> affordableIds, Set<Integer> unaffordableIds);

    /**
     * Highlights valid totem placement positions.
     *
     * @param validIndices valid offer-track indices
     * @param iAmOnOffer true if the local player's totem is already on the offer track
     */
    void highlightTotemPlacement(List<Integer> validIndices, boolean iAmOnOffer);

    /**
     * Disables all board interactions.
     */
    void disableAllInteractions();
}
