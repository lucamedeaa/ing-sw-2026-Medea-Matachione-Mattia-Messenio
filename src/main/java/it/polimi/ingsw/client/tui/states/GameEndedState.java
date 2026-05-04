package it.polimi.ingsw.client.tui.states;

import it.polimi.ingsw.client.tui.TUI;
import it.polimi.ingsw.client.tui.UIState;
import it.polimi.ingsw.client.tui.commands.CommandFactory;
import it.polimi.ingsw.client.tui.commands.DisconnectCommand;
import it.polimi.ingsw.client.tui.commands.GameCommand;

import java.util.HashMap;
import java.util.Map;

public class GameEndedState implements UIState {
    private final TUI tui;
    private final Map<String, CommandFactory> commandRegistry = new HashMap<>();

    public GameEndedState(TUI tui) {
        this.tui = tui;
        registerCommands();
    }

    private void registerCommands() {
        commandRegistry.put("0", args -> (GameCommand) () -> tui.changeState(new MatchmakingState(tui)));
        commandRegistry.put("d", args -> new DisconnectCommand(tui.getController()));
    }

    @Override
    public void render() {
        tui.renderGameEnded();
    }

    @Override
    public void handleInput(String input) {
        if (input == null || input.isBlank()) return;

        String[] parts = input.trim().split("\\s+");
        String commandKey = parts[0].toLowerCase();

        CommandFactory factory = commandRegistry.get(commandKey);
        if (factory == null) {
            tui.print("Comando non valido. Usa '0' (menu) o 'd' (disconnetti).");
            return;
        }

        try {
            GameCommand command = factory.create(parts);
            command.execute();
        } catch (Exception e) {
            tui.print("Errore: " + e.getMessage());
        }
    }
}