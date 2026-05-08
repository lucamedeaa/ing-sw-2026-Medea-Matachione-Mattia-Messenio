package it.polimi.ingsw.client.tui.states;

import it.polimi.ingsw.client.tui.NavigationPort;
import it.polimi.ingsw.client.tui.OutputPort;
import it.polimi.ingsw.client.tui.UIState;
import it.polimi.ingsw.client.tui.commands.CommandFactory;
import it.polimi.ingsw.client.tui.commands.DisconnectCommand;
import it.polimi.ingsw.client.tui.commands.GetLeaderboardCommand;
import it.polimi.ingsw.client.tui.commands.LeaveGameCommand;
import it.polimi.ingsw.client.tui.render.GameEndedRenderer;
import it.polimi.ingsw.client.view.listeners.GameEndedView;

import java.util.HashMap;
import java.util.Map;

public class GameEndedState implements UIState, GameEndedView {
    private final NavigationPort nav;
    private final OutputPort out;
    private final Map<String, CommandFactory> commandRegistry = new HashMap<>();

    private final GameEndedRenderer renderer;
    private boolean hasRendered = false;


    public GameEndedState(NavigationPort nav, OutputPort out) {
        this.nav = nav;
        this.out = out;
        this.renderer = new GameEndedRenderer(out);
        nav.getNotificationController().setGameEndedView(this);
        registerCommands();

        new GetLeaderboardCommand(nav.getController()).execute();
    }

    private void registerCommands() {
        commandRegistry.put("0", args -> new LeaveGameCommand(nav.getController(), out));
        commandRegistry.put("d", args -> new DisconnectCommand(nav.getController()));
    }

    @Override
    public void render() {
        nav.getMatchModel().getReadLock().lock();
        try {
            var local = nav.getMatchModel().getLocalResult();
            var global = nav.getMatchModel().getGlobalLeaderboard();

            if (local == null || global == null) {
                out.clearScreen();
                out.print("\n  \033[36;1mConsultando gli archivi della Valle di Mesos...\033[0m");
                return;
            }
            if (hasRendered) return;
            hasRendered = true;

            renderer.render(local, global);
        } finally {
            nav.getMatchModel().getReadLock().unlock();
        }
    }

    @Override
    public void onReturnToMatchmaking(String reason) {
        nav.getNotificationController().setGameEndedView(null);
        nav.changeState(new MatchmakingState(nav, out));
    }

    @Override
    public void handleInput(String input) {
        if (input == null || input.isBlank()) return;

        String[] parts = input.trim().split("\\s+");
        CommandFactory factory = commandRegistry.get(parts[0].toLowerCase());

        if (factory == null) {
            out.print("Comando non valido. Usa '0' (menu) o 'd' (disconnetti).");
            return;
        }

        try {
            factory.create(parts).execute();
        } catch (Exception e) {
            out.print("Errore: " + e.getMessage());
        }
    }
    @Override
    public void onServerDisconnected(String reason) {
        nav.getNotificationController().setGameEndedView(null);
        nav.changeState(new DisconnectedState(out, reason));
    }
}