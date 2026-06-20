package it.polimi.ingsw.client.view.tui.state;

import it.polimi.ingsw.client.model.ClientSession;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.view.tui.ApplicationLifecyclePort;
import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.client.view.tui.TuiNavigator;
import it.polimi.ingsw.client.view.tui.command.*;
import it.polimi.ingsw.client.view.tui.render.ColorAnsi;
import it.polimi.ingsw.client.view.tui.render.GameEndedRenderer;
import it.polimi.ingsw.client.view.listeners.GameEndedView;

import java.util.HashMap;
import java.util.Map;

/**
 * State that renders post-game results and handles leaderboard-related commands.
 */
public class GameEndedUiState implements UIState, GameEndedView {
    private final TuiNavigator navigator;
    private final GameModel gameModel;
    private final ServerCommandPort controller;
    private final OutputPort out;
    private final ClientNotificationController notificationController;

    private final Map<String, CommandFactory> commandRegistry = new HashMap<>();
    private final GameEndedRenderer renderer;
    private final ClientSession session;

    private boolean showLeaderboard = false;

    private final ApplicationLifecyclePort lifecyclePort;

    /**
     * Creates the game-ended state.
     *
     * @param navigator navigator used to return to matchmaking or open disconnection state
     * @param gameModel model containing completion and leaderboard data
     * @param controller server command port
     * @param session local client session
     * @param out output port used for rendering and feedback
     * @param notificationController notification controller used to receive game-ended events
     * @param lifecyclePort lifecycle port used by disconnect commands
     */
    public GameEndedUiState(TuiNavigator navigator, GameModel gameModel, ServerCommandPort controller, ClientSession session, OutputPort out, ClientNotificationController notificationController, ApplicationLifecyclePort lifecyclePort) {
        this.navigator = navigator;
        this.gameModel = gameModel;
        this.controller = controller;
        this.session = session;
        this.out = out;
        this.notificationController = notificationController;
        this.renderer = new GameEndedRenderer(out);
        this.lifecyclePort = lifecyclePort;

       // this.notificationController.setGameEndedView(this);
        registerCommands();
        //new GetLeaderboardCommand(controller).execute();
    }

    /** {@inheritDoc} */
    @Override
    public void onEnter() {
        this.notificationController.setGameEndedView(this);
    }

    /** {@inheritDoc} */
    @Override
    public void onExit() {
        this.notificationController.setGameEndedView(null);
    }

    private void registerCommands() {
        commandRegistry.put("0", args -> new LeaveGameCommand(controller, out));
        commandRegistry.put("d", args -> new DisconnectCommand(controller, lifecyclePort, out));
        commandRegistry.put("l", args -> new ShowLeaderboardCommand(this::showLeaderboard));
    }

    /** {@inheritDoc} */
    @Override
    public void render() {

            var local = gameModel.getLocalResult();
            var global = gameModel.getGlobalLeaderboard();
            var sessionScores = gameModel.getLeaderboard();

            if (local == null) {
                out.clearScreen();
                out.print(ColorAnsi.CYAN_BOLD + "\n Looking through the archives of the Mesos Valley..." + ColorAnsi.RESET);
                return;
            }


        renderer.render(local, sessionScores, showLeaderboard ? global : null, session.getNickname(), showLeaderboard);
    }

    private void showLeaderboard() {
        showLeaderboard = true;
        // Invia la richiesta, il server risponderà e il model farà scattare il render asincrono
        new GetLeaderboardCommand(controller).execute();
        render();
    }

    /** {@inheritDoc} */
    @Override
    public void onReturnToMatchmaking(String reason) {
       //notificationController.setGameEndedView(null);
        navigator.toMatchmaking();
    }

    /** {@inheritDoc} */
    @Override
    public void handleInput(String input) {
        if (input == null || input.isBlank()) return;

        String[] parts = input.trim().split("\\s+");
        CommandFactory factory = commandRegistry.get(parts[0].toLowerCase());

        if (factory == null) {
            out.print("Invalid command. Use “l” (leaderboard), “0” (menu) or “d” (log out).");
            return;
        }

        try {
            factory.create(parts).execute();
        } catch (Exception e) {
            out.print("Error: " + e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onServerDisconnected(String reason) {
        //notificationController.setGameEndedView(null);
        navigator.toDisconnected(reason);
    }
}
