package it.polimi.ingsw.client.view.tui.render;

import it.polimi.ingsw.client.model.snapshot.PlayerSnapshot;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.client.model.snapshot.PlayerResources;

import java.util.List;
import java.util.Map;

public class InGameRenderer {
    private final OutputPort out;

    public InGameRenderer(OutputPort out) {
        this.out = out;
    }

    public void render(
            GameModel gameModel,
            String myNickname,
            Map<String, PlayerResources> deltas,
            List<String> logs,
            String lastError
    ) {
        out.clearScreen();
        out.print("════ MESOS — Era " + gameModel.getCurrentEra() + " / Round " + gameModel.getCurrentRound() + " ════\n");

        renderRows(gameModel);
        out.print("");

        renderTracks(gameModel);
        renderPlayersBar(gameModel, myNickname);

        if (!myNickname.isEmpty()) {
            out.print("");
            renderPlayerTribe(gameModel, myNickname);
        }

        renderTurnRecap(deltas, gameModel);
        out.print("");

        if (!logs.isEmpty()) {
            out.print("── RECENT NOTIFICATIONS ──");
            for (String log : logs) {
                out.print("  " + log);
            }
            out.print("");
        }

        printActions(gameModel.getMyActions());

        if (lastError != null && !lastError.isEmpty()) {
            out.print(ColorAnsi.RED + "[ERROR] " + lastError + ColorAnsi.RESET);
        }

        if (gameModel.isGameOver()) {
            out.print(ColorAnsi.YELLOW_BOLD + "  ══ MATCH OVER — Press ENTER to see the results ══" + ColorAnsi.RESET);
        }

        out.prompt("> ");

    }

    private void printActions(List<ActionDto> actions) {
        out.print("\n" + ColorAnsi.YELLOW_BOLD + "── AVAILABLE ACTIONS ──> " + ColorAnsi.RESET);
        if (actions.isEmpty()) {
            out.print("  Wait for your turn...");
        } else {
            ActionRender renderer = new ActionRender(out);
            for (int i = 0; i < actions.size(); i++) {
                out.prompt("  " + ColorAnsi.CYAN_BOLD + "[ " + i + " ]" + ColorAnsi.RESET + " ");
                actions.get(i).accept(renderer);
            }
        }
        out.print("  " + ColorAnsi.CYAN_BOLD + "[ i ]" + ColorAnsi.RESET + " Guide to the cards");
        out.print("  " + ColorAnsi.CYAN_BOLD + "[ v <nome> ]" + ColorAnsi.RESET + " View a player's tribe");
        out.print("  " + ColorAnsi.RED_BOLD + "[ quit ]" + ColorAnsi.RESET + " Exit the game completely");
        out.print("  " + ColorAnsi.RED_BOLD + "[ leave ]" + ColorAnsi.RESET + " Exit the game and return to the menu\n");
    }


    private void renderRows(GameModel gameModel) {
        out.print("  UPPER ROW");
        CardBoxRenderer.printCardRow(out, gameModel.getUpperRowCards(), true);
        out.print("");
        out.print("  LOWER ROW");
        CardBoxRenderer.printCardRow(out, gameModel.getLowerRowCards(), true);
    }

    private void renderPlayersBar(GameModel gameModel, String myNickname) {
        out.print(ColorAnsi.WHITE_BOLD + "  PLAYERS" + ColorAnsi.RESET);
        for (PlayerSnapshot p : gameModel.getPlayers().values()) {
            boolean isMe = p.getNickname().equals(myNickname);
            boolean isActive = p.getNickname().equals(gameModel.getActivePlayer());

            String marker = isActive ? " " + ColorAnsi.GREEN_BOLD + "▶" + ColorAnsi.RESET : "  ";
            int tribeSize = gameModel.getTribes().getOrDefault(p.getNickname(), List.of()).size();
            String tag = isMe ? " " + ColorAnsi.ITALIC + "(you)" + ColorAnsi.RESET : "";

            String pColor = getTotemAnsiColor(gameModel, p.getNickname());

            out.print(String.format("%s %s%-14s" + ColorAnsi.RESET + "  food: " + ColorAnsi.GREEN_BOLD + "%2d" + ColorAnsi.RESET + "  prestige: " + ColorAnsi.GREEN_BOLD + "%3d" + ColorAnsi.RESET + "  sc. Buildings: " + ColorAnsi.GREEN_BOLD + "-%d" + ColorAnsi.RESET + "  sc. Food: " + ColorAnsi.GREEN_BOLD + "-%d" + ColorAnsi.RESET + "  [%d cards]%s",
                    marker, pColor, p.getNickname(), p.getFood(), p.getPrestige(), p.getFoodDiscount(), p.getSustenanceDiscount(), tribeSize, tag));
        }
    }

    public void renderPlayerTribe(GameModel model, String nickname) {
        List<Integer> tribe = model.getTribes().getOrDefault(nickname, List.of());
        out.print("  " + nickname.toUpperCase() + "'S TRIBE");
        CardBoxRenderer.printCardRow(out, tribe, false);
    }

    private void renderTurnRecap(Map<String, PlayerResources> deltas, GameModel gameModel)  {
        out.print("");
        out.print(ColorAnsi.WHITE_BOLD + "  TURN RECAP" + ColorAnsi.RESET);
        for (String nickname : gameModel.getPlayers().keySet()) {
            PlayerResources res = deltas.getOrDefault(nickname, new PlayerResources(0, 0, 0, 0));
            int df = res.food();
            int dp = res.prestige();
            int dd = res.discount();

            String foodStr = (df >= 0 ? "+" : "") + df;
            String ppStr   = (dp >= 0 ? "+" : "") + dp;
            //String discStr = dd == 0 ? "0" : (dd > 0 ? "-" + dd : "+" + Math.abs(dd));

            String pColor = getTotemAnsiColor(gameModel, nickname);

            String discStr = res.discount() == 0 ? "0" : (res.discount() > 0 ? "-" + res.discount() : "+" + Math.abs(res.discount()));
            String sustStr = res.sustenanceDiscount() == 0 ? "0" : (res.sustenanceDiscount() > 0 ? "-" + res.sustenanceDiscount() : "+" + Math.abs(res.sustenanceDiscount()));

            out.print(String.format("  %s%-14s" + ColorAnsi.RESET + "  food: %-3s prestige: %-3s sc. Buildings: " + ColorAnsi.GREEN_BOLD + "%-3s" + ColorAnsi.RESET + " sc. Food: " + ColorAnsi.GREEN_BOLD + "%-3s" + ColorAnsi.RESET,
                    pColor, nickname, foodStr, ppStr, discStr, sustStr));
        }
    }

    private void renderTracks(GameModel gameModel) {
        int pCount = Math.max(2, gameModel.getPlayers().size());

        int[] returnBonuses = switch(pCount) {
            case 2 -> new int[]{1, -1};
            case 3 -> new int[]{1, 0, -1};
            case 4 -> new int[]{2, 1, 0, -1};
            default -> new int[]{3, 1, 0, 0, -1};
        };

        String[] returnOccupants = new String[pCount];
        java.util.Arrays.fill(returnOccupants, "free");
        for (var e : gameModel.getReturnPositions().entrySet()) {
            if (e.getValue() >= 0 && e.getValue() < pCount) {
                returnOccupants[e.getValue()] = e.getKey();
            }
        }

        String offerLayout = switch(pCount) {
            case 2 -> "BCEF"; case 3 -> "BCDEF"; case 4 -> "BCDEFG"; default -> "ABCDEFG";
        };

        String[] offerOccupants = new String[offerLayout.length()];
        java.util.Arrays.fill(offerOccupants, "free");
        for (var e : gameModel.getTotemPositions().entrySet()) {
            if (e.getValue() >= 0 && e.getValue() < offerLayout.length()) {
                offerOccupants[e.getValue()] = e.getKey();
            }
        }

        out.print("  TURN ORDER TILE " + " ".repeat(pCount * 8 - 4) + "OFFER TRACK ──");

        StringBuilder top = new StringBuilder("  ");
        StringBuilder mid1 = new StringBuilder("  ");
        StringBuilder mid2 = new StringBuilder("  ");
        StringBuilder mid3 = new StringBuilder("  ");
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
            String color = getTotemAnsiColor(gameModel, returnOccupants[i]);
            mid3.append(color).append(player).append(ColorAnsi.RESET);

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
            String color = getTotemAnsiColor(gameModel, offerOccupants[i]);

            top.append("┌───────┐ ");
            mid1.append("│").append(specs[0]).append("│ ");
            mid2.append("│").append(specs[1]).append("│ ");
            mid3.append("│").append(color).append(player).append(ColorAnsi.RESET + "│ ");
            bot.append("└───────┘ ");
        }

        out.print(top.toString());
        out.print(mid1.toString());
        out.print(mid2.toString());
        out.print(mid3.toString());
        out.print(bot.toString());
        out.print("  * Note: if you cannot pay the penalties in food, you lose 2pp for each missing food item.\n");
    }

    private String getTotemAnsiColor(GameModel model, String nickname) {
        if (nickname.equals("free")) return ColorAnsi.GRAY;

        PlayerSnapshot player = model.getPlayers().get(nickname);
        if (player == null || player.getTotemColor() == null) return ColorAnsi.WHITE_BOLD;

        return switch (player.getTotemColor()) {
            case ORANGE -> ColorAnsi.ORANGE;
            case WHITE  -> ColorAnsi.WHITE_BOLD;
            case BLUE   -> ColorAnsi.BLUE_BOLD;
            case YELLOW -> ColorAnsi.YELLOW_BOLD;
            case BLACK  -> ColorAnsi.BLACK_BOLD;
        };
    }

    private String[] getTileSpecs(char id) {
        return switch (id) {
            case 'A' -> new String[]{" ↑0 ↓0 ", "  +3f  "};
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
}