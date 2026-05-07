package it.polimi.ingsw.client.tui.states;

import it.polimi.ingsw.client.tui.NavigationPort;
import it.polimi.ingsw.client.tui.OutputPort;
import it.polimi.ingsw.client.tui.UIState;
import it.polimi.ingsw.client.tui.commands.CommandFactory;
import it.polimi.ingsw.client.tui.commands.DisconnectCommand;
import it.polimi.ingsw.client.tui.render.GameEndedRenderer;

import java.util.HashMap;
import java.util.Map;

public class GameEndedState implements UIState {
    private final NavigationPort nav;
    private final OutputPort out;
    private final GameEndedRenderer renderer;
    private final Map<String, CommandFactory> commandRegistry = new HashMap<>();

    public GameEndedState(NavigationPort nav, OutputPort out) {
        this.nav = nav;
        this.out = out;
        this.renderer = new GameEndedRenderer(out);
        registerCommands();
    }

    private void registerCommands() {
        commandRegistry.put("0", args -> () -> nav.changeState(new MatchmakingState(nav, out)));
        commandRegistry.put("d", args -> new DisconnectCommand(nav.getController()));
    }

    @Override
    public void render() {
        renderer.render(
                nav.getModel().getWinners(),
                nav.getModel().getLeaderboard(),
                nav.getMyNickname()
        );
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
}