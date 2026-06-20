package it.polimi.ingsw.client.view.gui;

/**
 * Scene definitions used by the GUI router.
 */
public final class Scenes {
    private Scenes() {}

    /** Matchmaking scene definition. */
    public static final SceneDefinition MATCHMAKING  = new SceneDefinition("/fxml/MatchmakingScreen.fxml",   1280, 720, true);

    /** Lobby scene definition. */
    public static final SceneDefinition LOBBY        = new SceneDefinition("/fxml/LobbyScreen.fxml",         1280, 720, true);

    /** Main in-game scene definition. */
    public static final SceneDefinition IN_GAME      = new SceneDefinition("/fxml/inGame/InGameScreen.fxml", 1280, 720, false);

    /** Game-ended scene definition. */
    public static final SceneDefinition GAME_ENDED   = new SceneDefinition("/fxml/GameEndedScreen.fxml",     1280, 720, true);

    /** Disconnected scene definition. */
    public static final SceneDefinition DISCONNECTED = new SceneDefinition("/fxml/DisconnectedScreen.fxml",  640,  360, true);

    /** Information modal scene definition. */
    public static final SceneDefinition INFO         = new SceneDefinition("/fxml/InfoScreen.fxml",          800,  600, false);

}
