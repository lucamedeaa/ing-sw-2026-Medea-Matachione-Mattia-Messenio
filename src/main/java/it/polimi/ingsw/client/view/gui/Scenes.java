package it.polimi.ingsw.client.view.gui;

public final class Scenes {
    private Scenes() {}
    public static final SceneDefinition MATCHMAKING  = new SceneDefinition("/fxml/MatchmakingScreen.fxml",   1280, 720, true);
    public static final SceneDefinition LOBBY        = new SceneDefinition("/fxml/LobbyScreen.fxml",         1280, 720, true);
    public static final SceneDefinition IN_GAME      = new SceneDefinition("/fxml/inGame/InGameScreen.fxml", 1280, 720, false);
    public static final SceneDefinition GAME_ENDED   = new SceneDefinition("/fxml/GameEndedScreen.fxml",     1280, 720, true);
    public static final SceneDefinition DISCONNECTED = new SceneDefinition("/fxml/DisconnectedScreen.fxml",  640,  360, true);
    public static final SceneDefinition INFO         = new SceneDefinition("/fxml/InfoScreen.fxml",          800,  600, false);

}