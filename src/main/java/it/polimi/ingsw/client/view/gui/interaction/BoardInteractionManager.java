package it.polimi.ingsw.client.view.gui.interaction;

import java.util.List;
import java.util.Set;

/**
 * Coordinates board interaction modes and forwards confirmed selections to command ports.
 */
public class BoardInteractionManager {

    private final BoardCommandPort commands;
    private final BoardView boardPanel;

    private InteractionState currentState = InteractionState.IDLE;
    private int upperPicksAllowed = 0;
    private int lowerPicksAllowed = 0;

    /**
     * Creates a board interaction manager.
     *
     * @param commands command port used after a valid selection
     * @param boardPanel board view that exposes interaction highlights
     */
    public BoardInteractionManager(BoardCommandPort commands, BoardView boardPanel) {
        this.commands = commands;
        this.boardPanel = boardPanel;
    }

    /**
     * Enables card selection for the allowed rows and card sets.
     *
     * @param upper number of upper-row picks available
     * @param lower number of lower-row picks available
     * @param affordableIds cards that can be selected and paid
     * @param unaffordableIds cards that can be highlighted as unavailable
     */
    public void promptCardSelection(int upper, int lower, Set<Integer> affordableIds, Set<Integer> unaffordableIds) {
        currentState = InteractionState.SELECTING_CARD_TO_TAKE;
        upperPicksAllowed = upper;
        lowerPicksAllowed = lower;
        boardPanel.enableCardSelection(upper > 0, lower > 0, affordableIds, unaffordableIds);
    }

    /**
     * Handles a card selection from the board.
     *
     * @param row logical row index
     * @param col logical column index
     */
    public void onCardSelected(int row, int col) {
        if (currentState != InteractionState.SELECTING_CARD_TO_TAKE) return;
        if (row == 0 && upperPicksAllowed <= 0) return;
        if (row == 1 && lowerPicksAllowed <= 0) return;
        commands.takeCard(row, col);
        reset();
    }

    /**
     * Enables totem placement on the board.
     *
     * @param availableTiles valid offer-track tile indices
     * @param iAmOnOffer true if the local player's totem is already on the offer track
     */
    public void promptTotemPlacement(List<Integer> availableTiles, boolean iAmOnOffer) {
        currentState = InteractionState.SELECTING_TOTEM_POSITION;
        boardPanel.highlightTotemPlacement(availableTiles, iAmOnOffer);
    }

    /**
     * Handles an offer-track position selection.
     *
     * @param tileIndex selected tile index
     */
    public void onTotemPositionSelected(int tileIndex) {
        if (currentState != InteractionState.SELECTING_TOTEM_POSITION) return;
        commands.placeTotem(tileIndex);
        reset();
    }

    /**
     * Clears the current interaction mode and all board highlights.
     */
    public void reset() {
        currentState = InteractionState.IDLE;
        boardPanel.disableAllInteractions();
    }
}
