package it.polimi.ingsw.client.view.gui.controllers;

/**
 * Host contract for components that can change the player shown in the tribe panel.
 */
public interface ViewedPlayerHost {
    /**
     * Selects the player whose tribe should be displayed.
     *
     * @param nickname selected player nickname
     */
    void setViewedPlayer(String nickname);
}
