package it.polimi.ingsw.client.tui.states;

import it.polimi.ingsw.client.tui.TUI;
import it.polimi.ingsw.client.tui.UIState;
import it.polimi.ingsw.client.tui.commands.*;
import it.polimi.ingsw.network.messages.GameInfoDTO;

import java.util.*;

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

            int maxPlayers;
            try {
                maxPlayers = Integer.parseInt(args[args.length - 1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Errore: max_players deve essere un numero.");
            }

            if (maxPlayers < 2 || maxPlayers > 5) {
                throw new IllegalArgumentException("Errore: max_players deve essere tra 2 e 5.");
            }

            String nickname = String.join(" ", Arrays.copyOfRange(args, 1, args.length - 1));
            this.pendingNickname = nickname;

            return new CreateGameCommand(tui.getController(), tui, nickname, maxPlayers);
        });

        commandRegistry.put("join", args -> {
            if (args.length < 3) throw new IllegalArgumentException("Uso: join <nickname> <game_id>");

            String gameId = args[args.length - 1];
            String nickname = String.join(" ", Arrays.copyOfRange(args, 1, args.length - 1));

            this.pendingNickname = nickname;

            return new JoinGameCommand(tui.getController(), tui, nickname, gameId);
        });

        commandRegistry.put("list", args -> new AvailableGamesCommand(tui.getController(), tui));
        commandRegistry.put("disconnect", args -> new DisconnectCommand(tui.getController()));
        commandRegistry.put("0", args -> new DisconnectCommand(tui.getController()));
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

        CommandFactory factory = commandRegistry.get(commandKey);
        if (factory == null) {
            onError("Comando sconosciuto. Usa: create, join, list, 0.");
            return;
        }

        try {
            GameCommand command = factory.create(parts);
            command.execute();
        } catch (IllegalArgumentException e) {
            onError(e.getMessage());
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
