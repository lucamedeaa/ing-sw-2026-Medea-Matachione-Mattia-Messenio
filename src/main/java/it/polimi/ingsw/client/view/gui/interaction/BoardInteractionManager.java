package it.polimi.ingsw.client.view.gui.interaction;

import java.util.List;
import java.util.Set;

public class BoardInteractionManager {

    private final BoardCommandPort commands;
    private final BoardView boardPanel;

    private InteractionState currentState = InteractionState.IDLE;
    private int upperPicksAllowed = 0;
    private int lowerPicksAllowed = 0;

    public BoardInteractionManager(BoardCommandPort commands, BoardView boardPanel) {
        this.commands = commands;
        this.boardPanel = boardPanel;
    }

    public void promptCardSelection(int upper, int lower, Set<Integer> affordableIds, Set<Integer> unaffordableIds) {
        currentState = InteractionState.SELECTING_CARD_TO_TAKE;
        upperPicksAllowed = upper;
        lowerPicksAllowed = lower;
        boardPanel.enableCardSelection(upper > 0, lower > 0, affordableIds, unaffordableIds);
    }

    public void onCardSelected(int row, int col) {
        if (currentState != InteractionState.SELECTING_CARD_TO_TAKE) return;
        if (row == 0 && upperPicksAllowed <= 0) return;
        if (row == 1 && lowerPicksAllowed <= 0) return;
        commands.takeCard(row, col);
        reset();
    }

    public void promptTotemPlacement(List<Integer> availableTiles, boolean iAmOnOffer) {
        currentState = InteractionState.SELECTING_TOTEM_POSITION;
        boardPanel.highlightTotemPlacement(availableTiles, iAmOnOffer);
    }

    public void onTotemPositionSelected(int tileIndex) {
        if (currentState != InteractionState.SELECTING_TOTEM_POSITION) return;
        commands.placeTotem(tileIndex);
        reset();
    }

    public void reset() {
        currentState = InteractionState.IDLE;
        boardPanel.disableAllInteractions();
    }
}