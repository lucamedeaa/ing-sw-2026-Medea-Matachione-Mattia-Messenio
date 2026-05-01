package it.polimi.ingsw.client.tui.states;

import it.polimi.ingsw.client.tui.TUI;
import it.polimi.ingsw.client.tui.UIState;
import it.polimi.ingsw.network.messages.GameInfoDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MatchmakingState implements UIState {

    private final TUI tui;
    private List<GameInfoDTO> availableGames = new ArrayList<>();

    private String pendingNickname;
    private String pendingGameId;

    private final Map<String, Runnable> menuCommands;
    private Consumer<String> currentHandler;
    private final Consumer<String> rootHandler = this::handleMenu;

    public MatchmakingState(TUI tui) {
        this.tui = tui;
        menuCommands = Map.of(
                "1", () -> { tui.prompt("Nickname (o 'b' per annullare): ");  currentHandler = this::handleCreateNickname; },
                "2", () -> { tui.prompt("Game ID (o 'b' per annullare): ");   currentHandler = this::handleJoinGameId; },
                "3", () -> tui.getController().getAvailableGames(),
                "0", () -> { tui.getController().disconnect(); System.exit(0); }
        );
        currentHandler = rootHandler;
    }

    @Override
    public void render() {
        tui.renderMatchmaking(availableGames);
    }

    @Override
    public void handleInput(String input) {
        String cleanInput = input.trim();

        if (cleanInput.equalsIgnoreCase("b") && currentHandler != rootHandler) {

            this.pendingNickname = null;
            this.pendingGameId = null;

            this.currentHandler = rootHandler;
            render();
            return;
        }

        currentHandler.accept(cleanInput);
    }

    private void handleMenu(String input) {
        Runnable cmd = menuCommands.get(input);
        if (cmd != null) cmd.run();
        else tui.print("Invalid choice.");
    }

    private void handleCreateNickname(String input) {
        if (input.isEmpty()) { tui.prompt("Nickname (o 'b' per annullare): "); return; }
        pendingNickname = input;
        tui.prompt("Max players [2-5](o 'b' per annullare): ");
        currentHandler = this::handleCreateMaxPlayers;
    }

    private void handleCreateMaxPlayers(String input) {
        try {
            int max = Integer.parseInt(input);
            if (max < 2 || max > 5) {
                tui.print("Players must be between 2 and 5.");
                tui.prompt("Max players (o 'b' per annullare): ");
                return;
            }
            tui.setMyNickname(pendingNickname);
            tui.getController().createGame(pendingNickname, max);

            // Torna al menu in attesa della risposta del server
            currentHandler = rootHandler;
        } catch (NumberFormatException e) {
            tui.print("Invalid number.");
            tui.prompt("Max players [2-5] (o 'b' per annullare): ");
        }
    }

    private void handleJoinGameId(String input) {
        if (input.isEmpty()) { tui.prompt("Game ID (o 'b' per annullare): "); return; }
        pendingGameId = input;
        tui.prompt("Nickname (o 'b' per annullare): ");
        currentHandler = this::handleJoinNickname;
    }

    private void handleJoinNickname(String input) {
        if (input.isEmpty()) { tui.prompt("Nickname (o 'b' per annullare): "); return; }
        tui.setMyNickname(input);
        tui.getController().joinGame(input, pendingGameId);

        currentHandler = rootHandler;
    }

    @Override
    public void onMatchmakingSuccess(String text) {
        tui.changeState(new LobbyState(tui));
    }

    @Override
    public void onAvailableGames(List<GameInfoDTO> games) {
        availableGames = games;
        this.render();
    }

    @Override
    public void onError(String errorText) {
        currentHandler = this::handleMenu;
        tui.print("[ERROR] " + errorText);
        this.render();
    }
}