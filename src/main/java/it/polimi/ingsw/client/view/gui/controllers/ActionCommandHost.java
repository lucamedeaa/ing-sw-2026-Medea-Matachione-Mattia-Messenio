package it.polimi.ingsw.client.view.gui.controllers;

import java.util.List;
import java.util.Set;

/**
 * Operations exposed by the in-game screen to the actions panel.
 */
public interface ActionCommandHost {
    /**
     * Starts card selection on the board.
     *
     * @param upper upper-row picks available
     * @param lower lower-row picks available
     * @param affordableIds affordable selectable cards
     * @param unaffordableIds unaffordable selectable cards
     */
    void promptCardSelection(int upper, int lower, Set<Integer> affordableIds, Set<Integer> unaffordableIds);

    /**
     * Starts totem placement on the board.
     *
     * @param tiles valid offer-track tile indices
     */
    void promptTotemPlacement(List<Integer> tiles);

    /**
     * Sends a skip-action request.
     */
    void skipAction();

    /**
     * Leaves the current game.
     */
    void leaveGame();

    /**
     * Toggles the game log overlay.
     */
    void toggleLog();

    /**
     * Disconnects the client.
     */
    void disconnect();

    /**
     * Opens the information modal.
     */
    void showInfo();
}
