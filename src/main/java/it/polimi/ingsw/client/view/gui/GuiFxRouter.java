package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.model.UIObserver;
import it.polimi.ingsw.client.view.gui.controllers.ControllerRegistry;
import it.polimi.ingsw.client.view.gui.screen.*;
import javafx.application.Platform;
import javafx.stage.Stage;

public class GuiFxRouter implements UIObserver, GuiNavigator {

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

        ControllerRegistry registry = new ControllerRegistry(ctx, this);
        this.sceneLoader = new SceneLoader(stage, c -> {
            if (c == DisconnectedScreen.class)
                return new DisconnectedScreen(disconnectReason);
            return registry.createController(c);
        });

        setupCloseHandler();
    }

    private void navigateTo(SceneDefinition def) {
        if (Platform.isFxApplicationThread()) {
            applyNavigation(def);
        } else {
            Platform.runLater(() -> applyNavigation(def));
        }
    }

    private void applyNavigation(SceneDefinition def) {
        RefreshableScreen oldScreen = currentScreen;
        Object controller = sceneLoader.load(def);
        currentScreen = (controller instanceof RefreshableScreen r) ? r : null;
        if (oldScreen != null) oldScreen.onExit();
        if (currentScreen != null) currentScreen.onEnter();
    }

    @Override
    public void onStateChanged() {
        ctx.scheduler().runLater(() -> {
            if (currentScreen != null) currentScreen.refresh();
        });
    }

    @Override public void toMatchmaking() { navigateTo(Scenes.MATCHMAKING); }
    @Override public void toLobby() { navigateTo(Scenes.LOBBY); }
    @Override public void toInGame() { navigateTo(Scenes.IN_GAME); }
    @Override public void toGameEnded(){ navigateTo(Scenes.GAME_ENDED); }

    @Override
    public void toDisconnected(String reason) {
        this.disconnectReason = reason != null ? reason : "Connessione al server persa";
        navigateTo(Scenes.DISCONNECTED);
    }

    private void setupCloseHandler() {
        stage.setOnCloseRequest(event -> {
            if (currentScreen != null) {
                currentScreen.handleWindowClose(event);
            }
            if (!event.isConsumed()) {
                ctx.lifecycle().requestShutdown(); // fallback
            }
        });
    }
}