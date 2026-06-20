package it.polimi.ingsw.client.view.gui.controllers.board;

/**
 * Listener notified when the user selects a board element.
 */
public interface BoardSelectionListener {
    /**
     * Called when a card is selected.
     *
     * @param row logical row index
     * @param col logical column index
     */
    void onCardSelected(int row, int col);

    /**
     * Called when a totem position is selected.
     *
     * @param tileIndex selected offer-track tile index
     */
    void onTotemPositionSelected(int tileIndex);
}
