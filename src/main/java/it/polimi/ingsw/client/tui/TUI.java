package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.client.lightGameModel.LightGameModel;
import it.polimi.ingsw.client.lightGameModel.LightPlayer;
import it.polimi.ingsw.client.lightGameModel.UIObserver;
import it.polimi.ingsw.client.network.ServerController;
import it.polimi.ingsw.client.tui.render.ActionRender;
import it.polimi.ingsw.client.tui.render.CardBoxRenderer;
import it.polimi.ingsw.client.tui.states.MatchmakingState;
import it.polimi.ingsw.client.view.ClientUI;
import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.messages.GameInfoDTO;

import java.util.List;
import java.util.Map;
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


    public synchronized void renderInGame(List<AvailableActionDTO> actions, Map<String, int[]> deltas) {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("════ MESOS — Era " + model.getCurrentEra()
                + " / Round " + model.getCurrentRound() + " ════");
        System.out.println();
        renderRows();
        System.out.println();
        renderPlayersBar();
        if (!myNickname.isEmpty()) {
            System.out.println();
            renderPlayerTribe(myNickname);
        }
        renderTurnRecap(deltas);
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
            System.out.println("  i) Card reference guide");
        }
        System.out.print("> ");
    }

    private void renderRows() {
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

    public synchronized void renderTurnRecap(Map<String, int[]> deltas) {
        boolean anyChange = deltas.values().stream()
                .anyMatch(d -> d[0] != 0 || d[1] != 0);
        if (!anyChange) return;

        System.out.println();
        System.out.println("── TURN RECAP ──");
        for (var e : deltas.entrySet()) {
            int df = e.getValue()[0], dp = e.getValue()[1];
            if (df == 0 && dp == 0) continue;
            String foodStr = (df >= 0 ? "+" : "") + df + "f";
            String ppStr   = (dp >= 0 ? "+" : "") + dp + "pp";
            System.out.printf("  %-14s  food %s   prestige %s%n",
                    e.getKey(), foodStr, ppStr);
        }
    }

    public synchronized void renderCheatSheet() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║               MESOS — CARD REFERENCE                 ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("── CHARACTER TYPES ─────────────────────────────────────");
        System.out.println("  Builder    Costs food, gains prestige immediately");
        System.out.println("             e.g. -1f +2pp  /  -2f +5pp  (tier 1→3)");
        System.out.println("  Hunter     sym:✓ compatible  /  sym:✗ incompatible");
        System.out.println("             Hunt event: +1f  +N pp per hunter in tribe");
        System.out.println("  Artist     No direct effect — score via CavePaintings");
        System.out.println("  Shaman     ★x1 / ★x2 / ★x3  ritual stars");
        System.out.println("             ShamanicRitual: +Npp if ≥ threshold stars");
        System.out.println("  Collector  Sustenance: -3f food cost per collector");
        System.out.println("  Inventor   Has an icon — matching pair scores a bonus");
        System.out.println("             Icons: Spearhead Leather Bread Canoe Mortar");
        System.out.println("                    Rope Flute Statue Fishhook Necklace");
        System.out.println();
        System.out.println("── EVENTS ───────────────────────────────────────────────");
        System.out.println("  CavePaintings  ≥N artists → +N pp/artist | else -2pp");
        System.out.println("  Hunt           +1f  +N pp per hunter in tribe");
        System.out.println("  ShamanicRitual +N pp if enough stars | else -N pp");
        System.out.println("  Sustenance     -1f per character  (Collectors discount)");
        System.out.println("                 if food insufficient: -N pp per missing");
        System.out.println();
        System.out.println("── BUILDINGS ────────────────────────────────────────────");
        System.out.println("  Bought by spending food; give prestige on purchase.");
        System.out.println("  Era 1 (3–5f)  RitualShield  DiverseSet   InventorPair");
        System.out.println("                TurnBonus     FoodDsc(Art) FoodDsc(Col)");
        System.out.println("  Era 2 (5–7f)  ArtistFood    BuildrMastery RitualStars");
        System.out.println("                DblPrestige   FoodDsc(Inv) SetScorer");
        System.out.println("                HunterBonus");
        System.out.println("  Era 3 (6–10f) VictoryPoints LatePurchase");
        System.out.println("                ClassScorer(Inv/Art/Sha/Hun/Col/Bui)");
        System.out.println();
        System.out.println("── COMMANDS ─────────────────────────────────────────────");
        System.out.println("  N              select action N from the list");
        System.out.println("  N <tile>       place totem on tile index");
        System.out.println("  N <row> <col>  take card  (row: 0=upper  1=lower)");
        System.out.println("  i              open this reference guide");
        System.out.println("  q              return to game");
        System.out.println();
        System.out.println("────────────────────────────────────────────────────────");
        System.out.print("  Press Q to return to game > ");
    }

    public synchronized void print(String msg)  { System.out.println(msg); }
    public synchronized void prompt(String msg) { System.out.print(msg); }
}