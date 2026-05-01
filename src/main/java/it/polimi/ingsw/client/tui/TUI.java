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


    public synchronized void renderInGame(List<AvailableActionDTO> actions, Map<String, int[]> deltas, String lastError) {
        System.out.print("\033[H\033[2J");
        System.out.flush();

        System.out.println("════ MESOS — Era " + model.getCurrentEra()
                + " / Round " + model.getCurrentRound() + " ════");
        System.out.println();

        renderRows();
        System.out.println();

        renderTracks();

        renderPlayersBar();

        if (!myNickname.isEmpty()) {
            System.out.println();
            renderPlayerTribe(myNickname);
        }

        renderTurnRecap(deltas);
        System.out.println();

        List<String> logs = model.consumeGameLogs();
        if (!logs.isEmpty()) {
            System.out.println("── NOTIFICHE RECENTI ──");
            for (String log : logs) {
                System.out.println("  " + log);
            }
            System.out.println();
        }

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
            System.out.println("  v <nome>) View player's tribe");
        }

        System.out.println();

        if (lastError != null && !lastError.isEmpty()) {
            System.out.println("\033[31m[ERROR] " + lastError + "\033[0m");
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
        System.out.println("  PLAYERS ──");
        for (LightPlayer p : model.getPlayers().values()) {
            boolean isMe = p.getNickname().equals(myNickname);

            boolean isActive = p.getNickname().equals(model.getActivePlayer());
            String marker = isActive ? "► " : "  ";

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
        System.out.println();
        System.out.println("── TURN RECAP ──");
        for (var e : deltas.entrySet()) {
            int df = e.getValue()[0], dp = e.getValue()[1];
            String foodStr = (df >= 0 ? "+" : "") + df + "f";
            String ppStr   = (dp >= 0 ? "+" : "") + dp + "pp";
            System.out.printf("  %-14s  food %-4s prestige %s%n",
                    e.getKey(), foodStr, ppStr);
        }
    }

    public synchronized void renderCheatSheet() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                  MESOS — CARD REFERENCE                     │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println();
        System.out.println("── CHARACTER TYPES ────────────────────────────────────────────");
        System.out.println("  Builder    Grants food discount for buildings, gives PP at game end");
        System.out.println("             e.g. -1f discount, +2pp final");
        System.out.println("  Hunter     sym:✓ grants instant food equal to Hunters in tribe");
        System.out.println("             Hunt event: grants food per Hunter in tribe");
        System.out.println("  Artist     No direct effect — score via CavePaintings");
        System.out.println("  Shaman     ★x1 / ★x2 / ★x3  ritual stars");
        System.out.println("  Collector  Provides 3 food discount during the Sustenance event");
        System.out.println("  Inventor   Has an icon — matching pairs score ONLY with InventorPair");
        System.out.println("             Icons: Spearhead Leather Bread Canoe Mortar");
        System.out.println("                    Rope Flute Statue Fishhook Necklace");
        System.out.println();
        System.out.println("── EVENTS ─────────────────────────────────────────────────────");
        System.out.println("  CavePaintings  Thresholds and PP rewards depend on the specific card");
        System.out.println("  Hunt           Grants food based on Hunter count");
        System.out.println("  ShamanicRitual Highest stars gain PP, lowest lose PP (ties apply)");
        System.out.println("  Sustenance     -1f per character (Collectors discount)");
        System.out.println("                 if food insufficient: -2 pp per missing food");
        System.out.println();
        System.out.println("── BUILDINGS ──────────────────────────────────────────────────");
        System.out.println("  Bought by spending food; grant prestige at the end of the game.");
        System.out.println("  Era 1 (3-5f)  RitualShield  DiverseSet   InventorPair");
        System.out.println("                TurnBonus     FoodDsc(Art) FoodDsc(Col)");
        System.out.println("  Era 2 (5-7f)  ArtistFood    BuildrMastery RitualStars");
        System.out.println("                DblPrestige   FoodDsc(Inv) SetScorer");
        System.out.println("                HunterBonus");
        System.out.println("  Era 3 (6-10f) VictoryPoints LatePurchase");
        System.out.println("                ClassScorer(Inv/Art/Sha/Hun/Col/Bui)");
        System.out.println();
        System.out.println("── COMMANDS ───────────────────────────────────────────────────");
        System.out.println("  N              select action N from the list");
        System.out.println("  N <tile>       place totem on tile index");
        System.out.println("  N <row> <col>  take card  (row: 0=upper  1=lower)");
        System.out.println("  v <name>       view player's tribe");
        System.out.println("  i              open this reference guide");
        System.out.println("  q              return to game");
        System.out.println();
        System.out.print("  Press Q to return to game > ");

    }

    private void renderTracks() {
        int pCount = Math.max(2, model.getPlayers().size());

        // TURN ORDER TILE
        int[] returnBonuses = switch(pCount) {
            case 2 -> new int[]{1, -1};
            case 3 -> new int[]{1, 0, -1};
            case 4 -> new int[]{2, 1, 0, -1};
            default -> new int[]{3, 1, 0, 0, -1};
        };

        String[] returnOccupants = new String[pCount];
        java.util.Arrays.fill(returnOccupants, "free");
        for (var e : model.getReturnPositions().entrySet()) {
            if (e.getValue() >= 0 && e.getValue() < pCount) {
                returnOccupants[e.getValue()] = e.getKey();
            }
        }

        // OFFER TRACK
        String offerLayout = switch(pCount) {
            case 2 -> "BCEF"; case 3 -> "BCDEF"; case 4 -> "BCDEFG"; default -> "ABCDEFG";
        };

        String[] offerOccupants = new String[offerLayout.length()];
        java.util.Arrays.fill(offerOccupants, "free");
        for (var e : model.getTotemPositions().entrySet()) {
            if (e.getValue() >= 0 && e.getValue() < offerLayout.length()) {
                offerOccupants[e.getValue()] = e.getKey();
            }
        }

        //Rendering
        System.out.println("  TURN ORDER TILE " + " ".repeat(pCount * 8 - 4) + "OFFER TRACK ──");

        StringBuilder top = new StringBuilder("  ");
        StringBuilder mid1 = new StringBuilder("  "); // Ordine / Frecce picks
        StringBuilder mid2 = new StringBuilder("  "); // Cibo / Cibo tile A
        StringBuilder mid3 = new StringBuilder("  "); // Giocatore / Giocatore
        StringBuilder bot = new StringBuilder("  ");


        top.append("┌");
        mid1.append("│");
        mid2.append("│");
        mid3.append("│");
        bot.append("└");

        String[] ordinals = {"1st", "2nd", "3rd", "4th", "5th"};

        for (int i = 0; i < pCount; i++) {
            top.append("───────");
            mid1.append(centerString(ordinals[i], 7));

            String bonusStr = (returnBonuses[i] > 0 ? "+" : "") + returnBonuses[i] + "f";
            if (returnBonuses[i] == 0) bonusStr = "0f";
            mid2.append(centerString(bonusStr, 7));

            String player = centerString(returnOccupants[i], 7);
            String color = returnOccupants[i].equals("free") ? "\033[90m" : (returnOccupants[i].equals(myNickname) ? "\033[32m" : "\033[37m");
            mid3.append(color).append(player).append("\033[0m");

            bot.append("───────");

            if (i < pCount - 1) {
                top.append("┬");
                mid1.append("│");
                mid2.append("│");
                mid3.append("│");
                bot.append("┴");
            } else {
                top.append("┐   ");
                mid1.append("│   ");
                mid2.append("│   ");
                mid3.append("│   ");
                bot.append("┘   ");
            }
        }

        for (int i = 0; i < offerLayout.length(); i++) {
            char t = offerLayout.charAt(i);
            String[] specs = getTileSpecs(t);

            String player = centerString(offerOccupants[i], 7);
            String color = offerOccupants[i].equals("free") ? "\033[90m" : (offerOccupants[i].equals(myNickname) ? "\033[32m" : "\033[37m");

            top.append("┌───────┐ ");
            mid1.append("│").append(specs[0]).append("│ ");
            mid2.append("│").append(specs[1]).append("│ ");
            mid3.append("│").append(color).append(player).append("\033[0m│ ");
            bot.append("└───────┘ ");
        }

        System.out.println(top);
        System.out.println(mid1);
        System.out.println(mid2);
        System.out.println(mid3);
        System.out.println(bot);
        System.out.println("  * Nota: se non puoi pagare i malus in cibo, perdi 2pp per ogni cibo mancante.");
        System.out.println();
    }

    private String[] getTileSpecs(char id) {
        return switch (id) {
            case 'A' -> new String[]{" ↑0 ↓0 ", "  +3f  "}; // Tile 5 giocatori
            case 'B' -> new String[]{" ↑0 ↓1 ", "       "};
            case 'C' -> new String[]{" ↑1 ↓0 ", "       "};
            case 'D' -> new String[]{" ↑0 ↓2 ", "       "};
            case 'E' -> new String[]{" ↑1 ↓1 ", "       "};
            case 'F' -> new String[]{" ↑2 ↓0 ", "       "};
            case 'G' -> new String[]{" ↑2 ↓1 ", "       "};
            default  -> new String[]{"       ", "       "};
        };
    }

    private String centerString(String s, int width) {
        if (s.length() >= width) return s.substring(0, width);
        int pad = width - s.length();
        return " ".repeat(pad / 2) + s + " ".repeat(pad - pad / 2);
    }

    public synchronized void print(String msg)  { System.out.println(msg); }
    public synchronized void prompt(String msg) { System.out.print(msg); }
}