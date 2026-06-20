package it.polimi.ingsw.client.view.gui;

/**
 * Navigation contract for switching JavaFX scenes and opening modals.
 */
public interface GuiNavigator {
    /**
     * Opens the matchmaking screen.
     */
    void toMatchmaking();

    /**
     * Opens the lobby screen.
     */
    void toLobby();

    /**
     * Opens the in-game screen.
     */
    void toInGame();

    /**
     * Opens the game-ended screen.
     */
    void toGameEnded();

    /**
     * Opens the disconnected screen.
     *
     * @param reason disconnection reason to show
     */
    void toDisconnected(String reason);

    /**
     * Opens a modal scene.
     *
     * @param def scene definition to load as modal
     */
    void openModal(SceneDefinition def);
}
