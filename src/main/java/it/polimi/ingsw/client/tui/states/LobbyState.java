package it.polimi.ingsw.client.tui.states;

import it.polimi.ingsw.client.tui.TUI;
import it.polimi.ingsw.client.tui.UIState;
import it.polimi.ingsw.client.tui.commands.CommandFactory;
import it.polimi.ingsw.client.tui.commands.DisconnectCommand;
import it.polimi.ingsw.client.tui.commands.GameCommand;
import it.polimi.ingsw.client.tui.commands.LeaveGameCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobbyState implements UIState {
    private final TUI tui;
    private List<String> currentPlayers = new ArrayList<>();
    private String notification = "";
    private final Map<String, CommandFactory> commandRegistry = new HashMap<>();

    public LobbyState(TUI tui) {
        this.tui = tui;
        registerCommands();
    }

    private void registerCommands() {
        commandRegistry.put("0", args -> new LeaveGameCommand(tui.getController(), tui));
        commandRegistry.put("d", args -> new DisconnectCommand(tui.getController()));
    }

    @Override
    public void render() {
        tui.renderLobby(currentPlayers, notification);
    }

    @Override
    public void handleInput(String input) {
        if (input == null || input.isBlank()) return;

        String[] parts = input.trim().split("\\s+");
        String commandKey = parts[0].toLowerCase();

        CommandFactory factory = commandRegistry.get(commandKey);

        if (factory == null) {
            tui.print("Comando sconosciuto. Scegli '0' (Leave) o 'd' (Disconnect).");
            return;
        }

        try {
            GameCommand command = factory.create(parts);
            command.execute();
        } catch (Exception e) {
            tui.print("Errore: " + e.getMessage());
        }
    }

    @Override
    public void onRoomUpdate(List<String> players, String notification) {
        this.currentPlayers = players;
        this.notification = notification;
        this.render();
    }

    @Override
    public void onGameLeft() {
        tui.setMyNickname("");
        tui.changeState(new MatchmakingState(tui));
    }

    @Override
    public void onGameStarted() {
        tui.changeState(new InGameState(tui));
    }

    @Override
    public void onError(String errorText) {
        tui.print("[ERROR] " + errorText);
    }
}