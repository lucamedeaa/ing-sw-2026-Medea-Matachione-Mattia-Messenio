package it.polimi.ingsw.client.tui;
import it.polimi.ingsw.client.lightGameModel.LightGameModel;
import it.polimi.ingsw.client.lightGameModel.UIObserver;
import it.polimi.ingsw.client.network.ServerController;
import it.polimi.ingsw.client.view.ClientUI;
import it.polimi.ingsw.network.messages.GameInfoDTO;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public class TUI implements ClientUI, UIObserver {
    private UIState currentState;
    private ServerController controller;
    private final LightGameModel model;
    private final Scanner scanner = new Scanner(System.in);
    public TUI(LightGameModel model) { this.model = model; }

    public ServerController getController() {
        return this.controller;
    }

    public LightGameModel getModel() {
        return model;
    }

    public synchronized void changeState(UIState newState) {
        currentState = newState;
        currentState.render();
    }

    @Override
    public void setController(ServerController controller) {
        this.controller = controller;
    }

    @Override
    public void start() {
        changeState(new MatchmakingState(this));
        while (true) {
            String input = scanner.nextLine();
            synchronized (this) { currentState.handleInput(input); }
        }
    }

    @Override
     public synchronized void dispatch(Consumer<UIState> action) {
         action.accept(currentState);
     }

    @Override
    public synchronized void onStateChanged() {
        dispatch(UIState::onModelUpdated);
    }

    public synchronized void renderMatchmaking(List<GameInfoDTO> availableGames){
        System.out.print("\033[H\033[2J");
        System.out.println("╔══════════════════════════╗");
        System.out.println("║     MESOS — MENU         ║");
        System.out.println("╚══════════════════════════╝");
        System.out.println("  1. Create new game");
        System.out.println("  2. Join a game");
        System.out.println("  3. Available games");
        System.out.println("  0. Disconnect");
        if (!availableGames.isEmpty()) {
            System.out.println("Available games:");
            for (GameInfoDTO g : availableGames)
                System.out.println("  - " + g.getGameId() + " (" + g.getCurrentPlayers() + "/" + g.getMaxPlayers() + ")");
        }
        System.out.print("> ");
    }

    public synchronized void renderLobby(List<String> currentPlayers, String notification) {
        System.out.print("\033[H\033[2J");
        System.out.println("╔══════════════════════════╗");
        System.out.println("║    MESOS — LOBBY         ║");
        System.out.println("╚══════════════════════════╝");
        System.out.println("Players in lobby:");
        for (String p : currentPlayers) System.out.println("  • " + p);
        if (!notification.isEmpty()) System.out.println(notification);
        System.out.println("  0. Leave lobby");
        System.out.println("  d. Disconnect");
        System.out.print("> ");
    }

    public synchronized void renderInGame(){

    }
    public synchronized void print(String msg) { System.out.println(msg); }
    public synchronized void prompt(String msg) { System.out.print(msg); }
}