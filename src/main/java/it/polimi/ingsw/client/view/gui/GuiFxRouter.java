package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.model.UIObserver;
import it.polimi.ingsw.client.view.gui.controllers.*;
import it.polimi.ingsw.client.view.gui.screen.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GuiFxRouter implements UIObserver,GuiNavigator {
    private final Stage stage;
    private final GuiContext ctx;
    private RefreshableScreen currentScreen;
    private final Map<Class<?>, Supplier<Object>> factories = new HashMap<>();
    private String disconnectReason = "";

    public GuiFxRouter(Stage stage,GuiContext ctx) {
        this.stage = stage;
        this.ctx = ctx;
        ctx.lobbyModel().addObserver(this);
        ctx.gameModel().addObserver(this);
        registerControllers();
        setupCloseHandler();
    }

    private void navigateTo(SceneId sceneId) {
        if (Platform.isFxApplicationThread()) {
            loadScene(sceneId);
        } else {
            Platform.runLater(() -> loadScene(sceneId));
        }
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

    private void loadScene(SceneId sceneId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneId.path()));

            loader.setControllerFactory(controllerClass -> createController(controllerClass));
            Parent root = loader.load();
            Object fxmlController = loader.getController();

            if (fxmlController instanceof RefreshableScreen refreshableScreen) {
                currentScreen = refreshableScreen;
            } else {
                currentScreen = null;
            }

            stage.setScene(new Scene(root));

        } catch (IOException e) {
            throw new RuntimeException("Unable to load scene: " + sceneId.path(), e);
        }
    }

    private void registerControllers() {
        factories.put(MatchmakingScreen.class, () -> new MatchmakingScreen(ctx, this));
        factories.put(LobbyScreen.class,       () -> new LobbyScreen(ctx, this));
        factories.put(InGameScreen.class,      () -> new InGameScreen(ctx, this));
        factories.put(GameEndedScreen.class,   () -> new GameEndedScreen(ctx, this));
        factories.put(TribePanelController.class, TribePanelController::new);
        factories.put(DisconnectedScreen.class, () -> new DisconnectedScreen(disconnectReason));
        factories.put(BoardPanelController.class, BoardPanelController::new);
        factories.put(PlayersPanelController.class, PlayersPanelController::new);
        factories.put(ActionsPanelController.class, ActionsPanelController::new);
        factories.put(LogPanelController.class, LogPanelController::new);
    }

    private Object createController(Class<?> controllerClass) {
        Supplier<Object> factory = factories.get(controllerClass);
        if (factory != null) return factory.get();
        throw new RuntimeException("No factory registered for: " + controllerClass.getName());
    }


    private void setupCloseHandler() {
    stage.setOnCloseRequest(event -> {
        if (currentScreen != null) {
            currentScreen.handleWindowClose(event, ctx, this);
        }
    });
}
}




//Implementa GuiNavigator e UIObserver. Unica classe che conosce lo Stage, cambia scena e osserva gameModel.
// Si registra su gameModel una volta nel costruttore — mai rimosso.
// Tiene currentScreenObserver: solo toInGame() e toGameEnded() lo impostano con la screen corrente, tutti gli altri lo impostano a null.
// Quando onStateChanged() arriva dal thread di rete, fa Platform.runLater(() -> currentScreenObserver.onStateChanged()) —
// parallelo esatto di TextUserInterface.onStateChanged() nella TUI.
//Per ogni metodo toXxx(): 1. Carica FXML 2. Ottiene il controller 3. Se la screen deve ricevere refresh dal GameModel, assegna currentScreenObserver = controller 4. Altrimenti currentScreenObserver = null 5. stage.setScene(...)
