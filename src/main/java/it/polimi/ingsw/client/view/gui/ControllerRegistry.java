package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.view.gui.controllers.*;
import it.polimi.ingsw.client.view.gui.screen.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ControllerRegistry {

    private final GuiContext ctx;
    private final GuiNavigator navigator;
    private final Supplier<String> disconnectReason;
    private final Map<Class<?>, Supplier<Object>> factories = new HashMap<>();

    public ControllerRegistry(GuiContext ctx, GuiNavigator navigator, Supplier<String> disconnectReason) {
        this.ctx = ctx;
        this.navigator = navigator;
        this.disconnectReason = disconnectReason;
        register();
    }

    private void register() {
        factories.put(MatchmakingScreen.class,     () -> new MatchmakingScreen(ctx, navigator));
        factories.put(LobbyScreen.class,           () -> new LobbyScreen(ctx, navigator));
        factories.put(InGameScreen.class,          () -> new InGameScreen(ctx, navigator));
        factories.put(GameEndedScreen.class,       () -> new GameEndedScreen(ctx, navigator));
        factories.put(DisconnectedScreen.class,    () -> new DisconnectedScreen(disconnectReason.get()));
        factories.put(BoardPanelController.class,  BoardPanelController::new);
        factories.put(PlayersPanelController.class,PlayersPanelController::new);
        factories.put(ActionsPanelController.class,ActionsPanelController::new);
        factories.put(LogPanelController.class,    LogPanelController::new);
        factories.put(TribePanelController.class,  TribePanelController::new);
    }

    public Object createController(Class<?> controllerClass) {
        Supplier<Object> factory = factories.get(controllerClass);
        if (factory != null) return factory.get();
        throw new RuntimeException("No factory registered for: " + controllerClass.getName());
    }
}