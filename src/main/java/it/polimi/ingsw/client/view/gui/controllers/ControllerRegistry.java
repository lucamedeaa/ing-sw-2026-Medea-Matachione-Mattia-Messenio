package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.presenter.*;
import it.polimi.ingsw.client.view.gui.screen.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Factory registry used by the FXML loader to create GUI controllers with their presenters.
 */
public class ControllerRegistry {

    private final Map<Class<?>, Supplier<Object>> factories;

    /**
     * Creates a registry for the supplied GUI context and navigator.
     *
     * @param ctx shared GUI context
     * @param navigator scene navigator
     */
    public ControllerRegistry(GuiContext ctx, GuiNavigator navigator) {
        Map<Class<?>, Supplier<Object>> m = new HashMap<>();
        m.put(MatchmakingScreen.class, () -> new MatchmakingScreen(new MatchmakingPresenter(ctx, navigator)));
        m.put(LobbyScreen.class,       () -> new LobbyScreen(new LobbyPresenter(ctx, navigator)));
        m.put(InGameScreen.class, () -> new InGameScreen(new InGamePresenter(ctx, navigator), navigator::openModal));
        m.put(GameEndedScreen.class,   () -> new GameEndedScreen(new GameEndedPresenter(ctx, navigator)));
        m.put(BoardPanelController.class,   BoardPanelController::new);
        m.put(PlayersPanelController.class, PlayersPanelController::new);
        m.put(ActionsPanelController.class, ActionsPanelController::new);
        m.put(LogPanelController.class,     LogPanelController::new);
        m.put(TribePanelController.class,   TribePanelController::new);
        m.put(InfoScreen.class, InfoScreen::new);
        this.factories = Map.copyOf(m);
    }

    /**
     * Creates a controller instance for the requested class.
     *
     * @param c requested controller class
     * @return new controller instance
     */
    public Object createController(Class<?> c) {
        Supplier<Object> factory = factories.get(c);
        if (factory != null) return factory.get();
        throw new RuntimeException("No factory registered for: " + c.getName());
    }
}
