package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.client.lightGameModel.LightGameModel;
import it.polimi.ingsw.client.lightGameModel.LightPlayer;
import it.polimi.ingsw.client.lightGameModel.UIObserver;
import it.polimi.ingsw.client.network.ServerController;
import it.polimi.ingsw.client.tui.render.CardBoxRenderer;
import it.polimi.ingsw.client.view.ClientUI;
import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.messages.GameInfoDTO;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public class TUI implements ClientUI, UIObserver {
    private UIState currentState;
    private ServerController controller;
    private final LightGameModel model;
    private String myNickname = "";
    private final Scanner scanner = new Scanner(System.in);

    public TUI(LightGameModel model) {
        this.model = model;
        this.model.addObserver(this);
    }

    public ServerController getController() { return this.controller; }
    public LightGameModel getModel()        { return model; }
    public String getMyNickname()           { return myNickname; }
    public void setMyNickname(String n)     { this.myNickname = n; }

    public synchronized void changeState(UIState newState) {
        currentState = newState;
        currentState.render();
    }

    @Override
    public void setController(ServerController controller) { this.controller = controller; }

    @Override
    public void start() {
        changeState(new MatchmakingState(this));
        while (true) {
            String input = scanner.nextLine();
            synchronized (this) { currentState.handleInput(input); }
        }
    }

    @Override
    public synchronized void dispatch(Consumer<UIState> action) { action.accept(currentState); }

    @Override
    public synchronized void onStateChanged() { dispatch(UIState::onModelUpdated); }


    public synchronized void renderMatchmaking(List<GameInfoDTO> availableGames) {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("╔══════════════════════════╗");
        System.out.println("║     MESOS — MENU         ║");
        System.out.println("╚══════════════════════════╝");
        System.out.println("  1. Create new game");
        System.out.println("  2. Join a game");
        System.out.println("  3. Available games");
        System.out.println("  0. Disconnect");
        if (!availableGames.isEmpty()) {
            System.out.println("\nAvailable games:");
            for (GameInfoDTO g : availableGames)
                System.out.println("  - " + g.getGameId() + " (" + g.getCurrentPlayers() + "/" + g.getMaxPlayers() + ")");
        }
        System.out.print("> ");
    }


    public synchronized void renderLobby(List<String> currentPlayers, String notification) {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("╔══════════════════════════╗");
        System.out.println("║    MESOS — LOBBY         ║");
        System.out.println("╚══════════════════════════╝");
        System.out.println("Players in lobby:");
        for (String p : currentPlayers) System.out.println("  • " + p);
        if (notification != null && !notification.isEmpty()) System.out.println("\n" + notification);
        System.out.println("\n  0. Leave lobby");
        System.out.println("  d. Disconnect");
        System.out.print("> ");
    }


    public synchronized void renderInGame(List<AvailableActionDTO> actions) {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("════ MESOS — Era " + model.getCurrentEra()
                + " / Round " + model.getCurrentRound() + " ════");
        System.out.println();
        renderOfferTiles();
        System.out.println();
        renderPlayersBar();
        if (!myNickname.isEmpty()) {
            System.out.println();
            renderPlayerTribe(myNickname);
        }
        System.out.println();
        System.out.println("── AVAILABLE ACTIONS ──");
        if (actions.isEmpty()) {
            System.out.println("  Wait for your turn...");
        } else {
            ActionRender renderer = new ActionRender(this);
            for (int i = 0; i < actions.size(); i++) {
                System.out.print("  " + i + ") ");
                actions.get(i).accept(renderer);
            }
        }
        System.out.print("> ");
    }

    private void renderOfferTiles() {
        System.out.println("── UPPER ROW ──");
        CardBoxRenderer.printCardRow(model.getUpperRowCards());
        System.out.println();
        System.out.println("── LOWER ROW ──");
        CardBoxRenderer.printCardRow(model.getLowerRowCards());
    }

    private void renderPlayersBar() {
        System.out.println("── PLAYERS ──");
        boolean myTurn = !model.getMyActions().isEmpty();
        for (LightPlayer p : model.getPlayers().values()) {
            boolean isMe = p.getNickname().equals(myNickname);
            String marker = (isMe && myTurn) ? "▶ " : "  ";
            int tribeSize = model.getTribes().getOrDefault(p.getNickname(), List.of()).size();
            String tag = isMe ? " (you)" : "";
            System.out.printf("%s%-14s  food: %2d  prestige: %3d  [%d cards]%s%n",
                    marker, p.getNickname(), p.getFood(), p.getPrestige(), tribeSize, tag);
        }
    }

    public void renderPlayerTribe(String nickname) {
        List<Integer> tribe = model.getTribes().getOrDefault(nickname, List.of());
        System.out.println("── " + nickname.toUpperCase() + "'S TRIBE ──");
        CardBoxRenderer.printCardRow(tribe);
    }


    public synchronized void renderGameEnded() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("╔══════════════════════════╗");
        System.out.println("║   MESOS — GAME OVER      ║");
        System.out.println("╚══════════════════════════╝");
        System.out.println("Winners: " + String.join(", ", model.getWinners()));
        System.out.println();
        System.out.println("Final leaderboard:");
        var leaderboard = model.getLeaderboard();
        for (int i = 0; i < leaderboard.size(); i++)
            System.out.printf("  %d. %-14s %d PP%n",
                    i + 1, leaderboard.get(i).nickname(), leaderboard.get(i).finalScore());
        System.out.println();
        System.out.println("  Press ENTER to return to menu...");
    }


    public synchronized void print(String msg)  { System.out.println(msg); }
    public synchronized void prompt(String msg) { System.out.print(msg); }
}