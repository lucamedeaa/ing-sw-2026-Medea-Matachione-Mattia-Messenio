package it.polimi.ingsw.client.view.tui.state;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.client.view.tui.TuiNavigator;
import it.polimi.ingsw.client.view.tui.command.*;
import it.polimi.ingsw.client.view.tui.render.ColorAnsi;
import it.polimi.ingsw.client.view.tui.render.GameEndedRenderer;
import it.polimi.ingsw.client.view.listeners.GameEndedView;

import java.util.HashMap;
import java.util.Map;

public class GameEndedUiState implements UIState, GameEndedView {
    private final TuiNavigator navigator;
    private final GameModel gameModel;
    private final ServerCommandPort controller;
    private final OutputPort out;
    private final ClientNotificationController notificationController;

    private final Map<String, CommandFactory> commandRegistry = new HashMap<>();
    private final GameEndedRenderer renderer;
    private boolean hasRendered = false;


    public GameEndedUiState(TuiNavigator navigator, GameModel gameModel, ServerCommandPort controller, OutputPort out, ClientNotificationController notificationController) {
        this.navigator = navigator;
        this.gameModel = gameModel;
        this.controller = controller;
        this.out = out;
        this.notificationController = notificationController;
        this.renderer = new GameEndedRenderer(out);

        this.notificationController.setGameEndedView(this);
        registerCommands();
        new GetLeaderboardCommand(controller).execute();
    }

    private void registerCommands() {
        commandRegistry.put("0", args -> new LeaveGameCommand(controller, out));
        commandRegistry.put("d", args -> new DisconnectCommand(controller));
    }

    @Override
    public void render() {
        gameModel.getReadLock().lock();
        try {
            var local = gameModel.getLocalResult();
            var global = gameModel.getGlobalLeaderboard();

            if (local == null || global == null) {
                out.clearScreen();
                out.print(ColorAnsi.CYAN_BOLD + "\n Looking through the archives of the Mesos Valley..." + ColorAnsi.RESET);
                return;
            }
            if (hasRendered) return;
            hasRendered = true;

            renderer.render(local, global);
        } finally {
            gameModel.getReadLock().unlock();
        }
    }

    @Override
    public void onReturnToMatchmaking(String reason) {
        notificationController.setGameEndedView(null);
        navigator.toMatchmaking();
    }

    @Override
    public void handleInput(String input) {
        if (input == null || input.isBlank()) return;

        String[] parts = input.trim().split("\\s+");
        CommandFactory factory = commandRegistry.get(parts[0].toLowerCase());

        if (factory == null) {
            out.print("Invalid command. Use “0” (menu) or “d” (log out).");
            return;
        }

        try {
            factory.create(parts).execute();
        } catch (Exception e) {
            out.print("Error: " + e.getMessage());
        }
    }
    @Override
    public void onServerDisconnected(String reason) {
        notificationController.setGameEndedView(null);
        navigator.toDisconnected(reason);
    }
}