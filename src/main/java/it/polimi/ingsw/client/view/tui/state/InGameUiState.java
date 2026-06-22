package it.polimi.ingsw.client.view.tui.state;

import it.polimi.ingsw.client.model.ClientSession;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.snapshot.PlayerResources;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.view.tui.ApplicationLifecyclePort;
import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.client.view.tui.TuiNavigator;
import it.polimi.ingsw.client.view.tui.command.*;
import it.polimi.ingsw.client.view.tui.render.InGameRenderer;
import it.polimi.ingsw.client.view.listeners.InGameView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * State that manages the main in-game TUI screen and its local commands.
 */
public class InGameUiState implements UIState, InGameView {
    private final TuiNavigator navigator;
    private final GameModel gameModel;
    private final ServerCommandPort controller;
    private final ClientSession session;
    private final OutputPort out;
    private final ClientNotificationController notificationController;

    private final InGameRenderer renderer;
    private final Map<String, CommandFactory> commandRegistry = new HashMap<>();
    private final ApplicationLifecyclePort lifecyclePort;

    /**
     * Creates an in-game state.
     *
     * @param navigator navigator used for local screen transitions
     * @param gameModel model containing the game snapshot
     * @param controller server command port
     * @param session local client session
     * @param out output port used for rendering and feedback
     * @param notificationController notification controller used to receive in-game events
     * @param lifecyclePort lifecycle port used by disconnect commands
     */
    public InGameUiState(TuiNavigator navigator, GameModel gameModel, ServerCommandPort controller, ClientSession session, OutputPort out, ClientNotificationController notificationController, ApplicationLifecyclePort lifecyclePort) {
        this.navigator = navigator;
        this.gameModel = gameModel;
        this.controller = controller;
        this.session = session;
        this.out = out;
        this.notificationController = notificationController;
        this.renderer = new InGameRenderer(out);
        this.lifecyclePort = lifecyclePort;

        registerCommands();
    }

    /** {@inheritDoc} */
    @Override
    public void onEnter() {
        this.notificationController.setInGameView(this);
    }

    /** {@inheritDoc} */
    @Override
    public void onExit() {
        this.notificationController.setInGameView(null);
    }

    private void registerCommands() {
        commandRegistry.put("v", args -> {
            if (args.length < 2) {
                throw new IllegalArgumentException("Specify a player. Usage: v <nickname>");
            }
            if (!gameModel.getPlayers().containsKey(args[1])) {
                throw new IllegalArgumentException("Player not found: " + args[1]);
            }
            return new ViewTribeCommand(navigator, args[1]);
        });
        commandRegistry.put("i", args -> new InfoCommand(navigator));
        commandRegistry.put("quit", args -> new DisconnectCommand(controller, lifecyclePort, out));
        commandRegistry.put("leave", args -> new LeaveGameCommand(controller, out));
    }

    /** {@inheritDoc} */
    @Override
    public void render() {
        if (gameModel.getPlayers().isEmpty()) {
            return;
        }
        String error = gameModel.consumeGlobalError();
        Map<String, PlayerResources> deltas = gameModel.getTurnDeltas();
        List<String> logs = gameModel.consumeGameLogs();
        renderer.render(gameModel, session.getNickname(), deltas,logs, error);
    }

    /** {@inheritDoc} */
    @Override
    public void handleInput(String input) {
        if (gameModel.isGameOver()) {
            navigator.toGameEnded();
            return;
        }

        String[] parts = input.trim().split("\\s+");
        String key = parts[0].toLowerCase();

        try {
            if (key.matches("\\d+")) {
                new ActionCommand(gameModel, controller, out, parts).execute();
            } else {
                CommandFactory factory = commandRegistry.get(key);
                if (factory != null) {
                    factory.create(parts).execute();
                } else {
                    gameModel.setGlobalError("Unknown command.");
                }
            }
        } catch (IllegalArgumentException e) {
            gameModel.setGlobalError(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onError(String error) {
    }

    /** {@inheritDoc} */
    @Override
    public void onReturnToMatchmaking(String reason) {
        navigator.toMatchmaking();
    }

    /** {@inheritDoc} */
    @Override
    public void onServerDisconnected(String reason) {
        navigator.toDisconnected(reason);
    }


}
