package it.polimi.ingsw.client.tui.states;
import it.polimi.ingsw.client.tui.TUI;
import it.polimi.ingsw.client.tui.UIState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class LobbyState implements UIState {

    private final TUI tui;
    private List<String> currentPlayers = new ArrayList<>();
    private String notification = "";

    private final Map<String, Runnable> menuCommands;
    private Consumer<String> currentHandler;

    public LobbyState(TUI tui) {
        this.tui = tui;
        menuCommands = Map.of(
                "0", () -> tui.getController().leaveGame(),
                "d", () -> tui.getController().disconnect()
        );
        currentHandler = this::handleMenu;
    }

    @Override
    public void render() { tui.renderLobby(currentPlayers, notification); }

    @Override
    public void handleInput(String input) { currentHandler.accept(input.trim()); }

    private void handleMenu(String input) {
        Runnable cmd = menuCommands.get(input);
        if (cmd != null) cmd.run();
        else tui.print("Invalid choice.");
    }

    @Override
    public void onRoomUpdate(List<String> players, String notification) {
        this.currentPlayers = players;
        this.notification = notification;
        this.render();
    }

    @Override
    public void onGameLeft() { tui.changeState(new MatchmakingState(tui)); }

    @Override
    public void onGameStarted() { tui.changeState(new InGameState(tui)); }

    @Override
    public void onError(String errorText) {
        tui.print("[ERROR] " + errorText);
        this.render();
    }
}