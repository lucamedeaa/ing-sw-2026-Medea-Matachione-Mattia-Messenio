package it.polimi.ingsw.client.view.gui;

    public enum SceneId {
        MATCHMAKING("/fxml/MatchmakingScreen.fxml"),
        LOBBY("/fxml/LobbyScreen.fxml"),
        IN_GAME("/fxml/inGame/InGameScreen.fxml"),
        GAME_ENDED("/fxml/GameEndedScreen.fxml"),
        VIEW_TRIBE("/fxml/ViewTribeScreen.fxml"),
        INFO("/fxml/InfoScreen.fxml"),
        DISCONNECTED("/fxml/DisconnectedScreen.fxml");

        private final String path;

        SceneId(String path) {
            this.path = path;
        }

        public String path() {
            return path;
        }
    }
