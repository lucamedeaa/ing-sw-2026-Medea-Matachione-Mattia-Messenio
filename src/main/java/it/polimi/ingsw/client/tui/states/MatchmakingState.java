package it.polimi.ingsw.client.tui.states;

import it.polimi.ingsw.client.tui.TUI;
import it.polimi.ingsw.client.tui.UIState;
import it.polimi.ingsw.client.tui.commands.*;
import it.polimi.ingsw.network.messages.GameInfoDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchmakingState implements UIState {
    private final TUI tui;
    private List<GameInfoDTO> availableGames = new ArrayList<>();
    private final Map<String, CommandFactory> commandRegistry = new HashMap<>();
    private String lastError = "";
    private String pendingNickname = "";

    public MatchmakingState(TUI tui) {
        this.tui = tui;
        registerCommands();
    }

    private void registerCommands() {
        commandRegistry.put("create", args -> {
            if (args.length < 3) throw new IllegalArgumentException("Uso: create <nickname> <max_players>");
            this.pendingNickname = args[1]; // SALVA TEMPORANEAMENTE
            return new CreateGameCommand(tui.getController(), tui, args[1], Integer.parseInt(args[2]));
        });

        commandRegistry.put("join", args -> {
            if (args.length < 3) throw new IllegalArgumentException("Uso: join <nickname> <game_id>");
            this.pendingNickname = args[1]; // SALVA TEMPORANEAMENTE
            return new JoinGameCommand(tui.getController(), tui, args[1], args[2]);
        });

        commandRegistry.put("list", args -> new AvailableGamesCommand(tui.getController(), tui));


        commandRegistry.put("disconnect", args -> new DisconnectCommand(tui.getController()));
        commandRegistry.put("0", args -> new DisconnectCommand(tui.getController())); // Alias
    }



    @Override
    public void render() {
        tui.renderMatchmaking(availableGames);
        if (!lastError.isEmpty()) {
            tui.print("\033[31m[ERROR] " + lastError + "\033[0m");
            lastError = "";
        }
    }

    @Override
    public void handleInput(String input) {
        if (input == null || input.isBlank()) return;

        String[] parts = input.trim().split("\\s+");
        String commandKey = parts[0].toLowerCase();

        if (commandKey.equals("create")) {
            if (parts.length < 3) {
                onError("Uso: create <nickname> <max_players>");
                return;
            }
            try {
                int p = Integer.parseInt(parts[2]);
                if (p < 2 || p > 5) {
                    onError("Errore: max_players deve essere tra 2 e 5.");
                    return;
                }
            } catch (NumberFormatException e) {
                onError("Errore: max_players deve essere un numero.");
                return;
            }
            this.pendingNickname = parts[1]; // Salva il nickname temp
        }

        if (commandKey.equals("join")) {
            if (parts.length < 3) {
                onError("Uso: join <nickname> <game_id>");
                return;
            }
            this.pendingNickname = parts[1]; // Salva il nickname temp
        }

        // 2. Lookup ed Esecuzione
        CommandFactory factory = commandRegistry.get(commandKey);
        if (factory == null) {
            onError("Comando sconosciuto. Usa: create, join, list, 0.");
            return;
        }

        try {
            GameCommand command = factory.create(parts);
            command.execute();
        } catch (Exception e) {
            onError("Errore durante l'esecuzione: " + e.getMessage());
        }
    }

    @Override
    public void onMatchmakingSuccess(String text) {
        tui.setMyNickname(this.pendingNickname);
        tui.changeState(new LobbyState(tui));
    }

    @Override
    public void onAvailableGames(List<GameInfoDTO> games) {
        this.availableGames = games;
        this.render();
    }

    @Override
    public void onError(String errorText) {
        this.pendingNickname = "";
        this.lastError = errorText;
        this.render();
    }
}