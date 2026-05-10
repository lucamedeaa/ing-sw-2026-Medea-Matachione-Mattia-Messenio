package it.polimi.ingsw.client.view.tui.state;

import it.polimi.ingsw.client.model.ClientSession;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.client.view.tui.TuiNavigator;
import it.polimi.ingsw.client.view.tui.command.*;
import it.polimi.ingsw.client.view.tui.render.InGameRenderer;
import it.polimi.ingsw.client.view.listeners.InGameView;

import java.util.HashMap;
import java.util.Map;

public class InGameUiState implements UIState, InGameView {
    private final TuiNavigator navigator;
    private final GameModel gameModel;
    private final ServerCommandPort controller;
    private final ClientSession session;
    private final OutputPort out;
    private final ClientNotificationController notificationController;

    private final InGameRenderer renderer;
    private final Map<String, CommandFactory> commandRegistry = new HashMap<>();

    public InGameUiState(TuiNavigator navigator, GameModel gameModel, ServerCommandPort controller, ClientSession session, OutputPort out, ClientNotificationController notificationController) {
        this.navigator = navigator;
        this.gameModel = gameModel;
        this.controller = controller;
        this.session = session;
        this.out = out;
        this.notificationController = notificationController;
        this.renderer = new InGameRenderer(out);

        registerCommands();
        this.notificationController.setInGameView(this);
    }

    private void registerCommands() {
        commandRegistry.put("v", args -> {
            if (args.length < 2) {
                throw new IllegalArgumentException("Specify a player. Usage: v <nickname>");
            }
            if (!gameModel.getPlayers().containsKey(args[1])) {
                throw new IllegalArgumentException("Player not found: " + args[1]);
            }
            return new ViewTribeCommand(navigator, out, args[1]);
        });
        commandRegistry.put("i", args -> new InfoCommand(navigator, out));
        commandRegistry.put("quit", args -> new DisconnectCommand(controller));
        commandRegistry.put("leave", args -> new LeaveGameCommand(controller, out));
    }

    @Override
    public void render() {
        if (gameModel.getPlayers().isEmpty()) {
            return;
        }
        String error = gameModel.consumeGlobalError();
        renderer.render(gameModel, session.getNickname(), gameModel.getTurnDeltas(), error);
    }

    @Override
    public void handleInput(String input) {
        if (gameModel.isGameOver()) {
            notificationController.setInGameView(null);
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

    @Override
    public void onError(String error) {
    }

    @Override
    public void onReturnToMatchmaking(String reason) {
        notificationController.setInGameView(null);
        navigator.toMatchmaking();
    }

    @Override
    public void onServerDisconnected(String reason) {
        notificationController.setInGameView(null);
        navigator.toDisconnected(reason);
    }


}