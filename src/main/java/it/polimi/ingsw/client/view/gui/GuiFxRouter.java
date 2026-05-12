package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.model.UIObserver;
import it.polimi.ingsw.client.view.gui.controllers.*;
import it.polimi.ingsw.client.view.gui.screen.*;
import javafx.application.Platform;
import javafx.stage.Stage;


public class GuiFxRouter implements UIObserver,GuiNavigator {
    private final Stage stage;
    private final GuiContext ctx;
    private RefreshableScreen currentScreen;
    private String disconnectReason = "";
    private final SceneLoader sceneLoader;

    public GuiFxRouter(Stage stage, GuiContext ctx) {
        this.stage = stage;
        this.ctx = ctx;
        ctx.lobbyModel().addObserver(this);
        ctx.gameModel().addObserver(this);
        ControllerRegistry registry = new ControllerRegistry(ctx, this, () -> this.disconnectReason);
        this.sceneLoader = new SceneLoader(stage, registry::createController);
        setupCloseHandler();
    }

    private void navigateTo(SceneId sceneId) {
        if (Platform.isFxApplicationThread()) {
            applyNavigation(sceneId);
        } else {
            Platform.runLater(() -> applyNavigation(sceneId));
        }
    }

    private void applyNavigation(SceneId sceneId) {
        Object controller = sceneLoader.load(sceneId);
        currentScreen = (controller instanceof RefreshableScreen r) ? r : null;
    }

    @Override
    public void onStateChanged() {
        Platform.runLater(() -> {
            if (currentScreen != null) {
                currentScreen.refresh();
            }
        });
    }

    @Override
    public void toMatchmaking() {
        navigateTo(SceneId.MATCHMAKING);
    }

    @Override
    public void toLobby() {
        navigateTo(SceneId.LOBBY);
    }

    @Override
    public void toInGame() {
        navigateTo(SceneId.IN_GAME);
    }

    @Override
    public void toGameEnded() {
       navigateTo(SceneId.GAME_ENDED);
    }

    @Override
    public void toDisconnected(String reason) {
        this.disconnectReason = reason != null ? reason : "";
        navigateTo(SceneId.DISCONNECTED);
    }

    private void setupCloseHandler() {
    stage.setOnCloseRequest(event -> {
        if (currentScreen != null) {
            currentScreen.handleWindowClose(event, ctx, this);
        }
    });
}
}
