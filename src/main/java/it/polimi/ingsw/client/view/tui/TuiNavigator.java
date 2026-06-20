package it.polimi.ingsw.client.view.tui;

/**
 * Navigation contract for switching between TUI screens.
 */
public interface TuiNavigator {
    /**
     * Opens the matchmaking screen.
     */
    void toMatchmaking();

    /**
     * Opens the lobby screen for the current room.
     */
    void toLobby();

    /**
     * Opens the in-game screen.
     */
    void toInGame();

    /**
     * Opens the tribe inspection screen for the selected player.
     *
     * @param targetPlayer nickname of the player whose tribe must be shown
     */
    void toViewTribe(String targetPlayer);

    /**
     * Opens the card reference screen.
     */
    void toInfo();

    /**
     * Opens the end-game results screen.
     */
    void toGameEnded();

    /**
     * Opens the disconnected screen.
     *
     * @param reason textual reason reported for the disconnection
     */
    void toDisconnected(String reason);
}
