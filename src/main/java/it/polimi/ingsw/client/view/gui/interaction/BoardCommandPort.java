package it.polimi.ingsw.client.view.gui.interaction;

/**
 * Command port used by board interactions to send selected moves.
 */
public interface BoardCommandPort {
    /**
     * Sends a take-card command.
     *
     * @param row logical row index, where 0 is upper and 1 is lower
     * @param col logical column index in the selected row
     */
    void takeCard(int row, int col);

    /**
     * Sends a place-totem command.
     *
     * @param position selected offer-track position
     */
    void placeTotem(int position);
}
