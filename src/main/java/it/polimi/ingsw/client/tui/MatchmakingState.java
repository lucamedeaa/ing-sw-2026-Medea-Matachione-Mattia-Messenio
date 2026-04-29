package it.polimi.ingsw.client.tui;
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

    public MatchmakingState(TUI tui) {
        this.tui = tui;
        menuCommands = Map.of(
                "1", () -> { tui.prompt("Nickname: ");  currentHandler = this::handleCreateNickname; },
                "2", () -> { tui.prompt("Game ID: ");   currentHandler = this::handleJoinGameId; },
                "3", () -> tui.getController().getAvailableGames(),
                "0", () -> { tui.getController().disconnect(); System.exit(0); }
        );
        currentHandler = this::handleMenu;
    }

    @Override
    public void render() { tui.renderMatchmaking(availableGames); }

    @Override
    public void handleInput(String input) { currentHandler.accept(input.trim()); }

    private void handleMenu(String input) {
        Runnable cmd = menuCommands.get(input);
        if (cmd != null) cmd.run();
        else tui.print("Invalid choice.");
    }

    private void handleCreateNickname(String input) {
        pendingNickname = input;
        tui.prompt("Max players: ");
        currentHandler = this::handleCreateMaxPlayers;
    }

    private void handleCreateMaxPlayers(String input) {
        try {
            tui.getController().createGame(pendingNickname, Integer.parseInt(input));
            currentHandler = this::handleMenu;
        } catch (NumberFormatException e) {
            tui.print("Invalid number.");
            tui.prompt("Max players: ");
        }
    }

    private void handleJoinGameId(String input) {
        pendingGameId = input;
        tui.prompt("Nickname: ");
        currentHandler = this::handleJoinNickname;
    }

    private void handleJoinNickname(String input) {
        tui.getController().joinGame(pendingGameId, input);
        currentHandler = this::handleMenu;
    }

    @Override
    public void onMatchmakingSuccess(String text) { tui.changeState(new LobbyState(tui)); }

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