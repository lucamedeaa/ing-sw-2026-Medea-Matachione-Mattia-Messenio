package it.polimi.ingsw.client.tui.render;

import it.polimi.ingsw.client.lightGameModel.LightPlayer;
import it.polimi.ingsw.client.lightGameModel.MatchModel;
import it.polimi.ingsw.client.tui.OutputPort;
import it.polimi.ingsw.client.tui.states.InGameState;
import it.polimi.ingsw.network.dto.AvailableActionDTO;

import java.util.List;
import java.util.Map;

public class InGameRenderer {
    private final OutputPort out;

    public InGameRenderer(OutputPort out) {
        this.out = out;
    }

    public void render(MatchModel matchModel, String myNickname, Map<String, InGameState.PlayerResources> deltas, String lastError) {
        out.clearScreen();
        out.print("════ MESOS — Era " + matchModel.getCurrentEra() + " / Round " + matchModel.getCurrentRound() + " ════\n");

        renderRows(matchModel);
        out.print("");

        renderTracks(matchModel);
        renderPlayersBar(matchModel, myNickname);

        if (!myNickname.isEmpty()) {
            out.print("");
            renderPlayerTribe(matchModel, myNickname);
        }

        renderTurnRecap(deltas, matchModel);
        out.print("");

        List<String> logs = matchModel.consumeGameLogs();
        if (!logs.isEmpty()) {
            out.print("── NOTIFICHE RECENTI ──");
            for (String log : logs) {
                out.print("  " + log);
            }
            out.print("");
        }

        printActions(matchModel.getMyActions());

        if (lastError != null && !lastError.isEmpty()) {
            out.print(AnsiColors.RED + "[ERROR] " + lastError + AnsiColors.RESET);
        }

        if (matchModel.isGameOver()) {
            out.print(AnsiColors.YELLOW_BOLD + "  ══ PARTITA TERMINATA — Premi INVIO per vedere i risultati ══" + AnsiColors.RESET);
        }

        out.prompt("> ");

    }

    private void printActions(List<AvailableActionDTO> actions) {
        out.print("\n" + AnsiColors.YELLOW_BOLD + "── AVAILABLE ACTIONS ──> " + AnsiColors.RESET);
        if (actions.isEmpty()) {
            out.print("  Wait for your turn...");
        } else {
            ActionRender renderer = new ActionRender(out);
            for (int i = 0; i < actions.size(); i++) {
                out.prompt("  " + AnsiColors.CYAN_BOLD + "[ " + i + " ]" + AnsiColors.RESET + " ");
                actions.get(i).accept(renderer);
            }
        }
        out.print("  " + AnsiColors.CYAN_BOLD + "[ i ]" + AnsiColors.RESET + " Guida alle carte");
        out.print("  " + AnsiColors.CYAN_BOLD + "[ v <nome> ]" + AnsiColors.RESET + " Guarda la tribù di un giocatore");
        out.print("  " + AnsiColors.RED_BOLD + "[ quit ]" + AnsiColors.RESET + " Chiudi definitivamente il gioco");
        out.print("  " + AnsiColors.RED_BOLD + "[ leave ]" + AnsiColors.RESET + " Abbandona la partita e torna al menu\n");
    }


    private void renderRows(MatchModel matchModel) {
        out.print("  UPPER ROW");
        CardBoxRenderer.printCardRow(out, matchModel.getUpperRowCards(), true);
        out.print("");
        out.print("  LOWER ROW");
        CardBoxRenderer.printCardRow(out, matchModel.getLowerRowCards(), true);
    }

    private void renderPlayersBar(MatchModel matchModel, String myNickname) {
        out.print(AnsiColors.WHITE_BOLD + "  PLAYERS" + AnsiColors.RESET);
        for (LightPlayer p : matchModel.getPlayers().values()) {
            boolean isMe = p.getNickname().equals(myNickname);
            boolean isActive = p.getNickname().equals(matchModel.getActivePlayer());

            String marker = isActive ? " " + AnsiColors.GREEN_BOLD + "▶" + AnsiColors.RESET : "  ";
            int tribeSize = matchModel.getTribes().getOrDefault(p.getNickname(), List.of()).size();
            String tag = isMe ? " " + AnsiColors.ITALIC + "(you)" + AnsiColors.RESET : "";

            String pColor = getTotemAnsiColor(matchModel, p.getNickname());

            out.print(String.format("%s %s%-14s" + AnsiColors.RESET + "  cibo: " + AnsiColors.GREEN_BOLD + "%2d" + AnsiColors.RESET + "  prestigio: " + AnsiColors.GREEN_BOLD + "%3d" + AnsiColors.RESET + "  sconto: " + AnsiColors.GREEN_BOLD + "-%d" + AnsiColors.RESET + "  [%d carte]%s",
                    marker, pColor, p.getNickname(), p.getFood(), p.getPrestige(), p.getFoodDiscount(), tribeSize, tag));
        }
    }

    public void renderPlayerTribe(MatchModel model, String nickname) {
        List<Integer> tribe = model.getTribes().getOrDefault(nickname, List.of());
        out.print("  " + nickname.toUpperCase() + "'S TRIBE");
        CardBoxRenderer.printCardRow(out, tribe, false);
    }

    private void renderTurnRecap(Map<String, InGameState.PlayerResources> deltas, MatchModel matchModel) {
        out.print("");
        out.print(AnsiColors.WHITE_BOLD + "  TURN RECAP" + AnsiColors.RESET);
        for (var e : deltas.entrySet()) {
            InGameState.PlayerResources res = e.getValue();
            int df = res.food();
            int dp = res.prestige();
            int dd = res.discount();

            String foodStr = (df >= 0 ? "+" : "") + df;
            String ppStr   = (dp >= 0 ? "+" : "") + dp;
            String discStr = dd == 0 ? "0" : (dd > 0 ? "-" + dd : "+" + Math.abs(dd));

            String pColor = getTotemAnsiColor(matchModel, e.getKey());

            out.print(String.format("  %s%-14s" + AnsiColors.RESET + "  cibo: %-3s prestigio: %-3s sconto: " + AnsiColors.GREEN_BOLD + "%-3s" + AnsiColors.RESET,
                    pColor, e.getKey(), foodStr, ppStr, discStr));
        }
    }

    private void renderTracks(MatchModel matchModel) {
        int pCount = Math.max(2, matchModel.getPlayers().size());

        int[] returnBonuses = switch(pCount) {
            case 2 -> new int[]{1, -1};
            case 3 -> new int[]{1, 0, -1};
            case 4 -> new int[]{2, 1, 0, -1};
            default -> new int[]{3, 1, 0, 0, -1};
        };

        String[] returnOccupants = new String[pCount];
        java.util.Arrays.fill(returnOccupants, "free");
        for (var e : matchModel.getReturnPositions().entrySet()) {
            if (e.getValue() >= 0 && e.getValue() < pCount) {
                returnOccupants[e.getValue()] = e.getKey();
            }
        }

        String offerLayout = switch(pCount) {
            case 2 -> "BCEF"; case 3 -> "BCDEF"; case 4 -> "BCDEFG"; default -> "ABCDEFG";
        };

        String[] offerOccupants = new String[offerLayout.length()];
        java.util.Arrays.fill(offerOccupants, "free");
        for (var e : matchModel.getTotemPositions().entrySet()) {
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
            String color = getTotemAnsiColor(matchModel, returnOccupants[i]);
            mid3.append(color).append(player).append(AnsiColors.RESET);

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
            String color = getTotemAnsiColor(matchModel, offerOccupants[i]);

            top.append("┌───────┐ ");
            mid1.append("│").append(specs[0]).append("│ ");
            mid2.append("│").append(specs[1]).append("│ ");
            mid3.append("│").append(color).append(player).append(AnsiColors.RESET + "│ ");
            bot.append("└───────┘ ");
        }

        out.print(top.toString());
        out.print(mid1.toString());
        out.print(mid2.toString());
        out.print(mid3.toString());
        out.print(bot.toString());
        out.print("  * Nota: se non puoi pagare i malus in cibo, perdi 2pp per ogni cibo mancante.\n");
    }

    private String getTotemAnsiColor(MatchModel model, String nickname) {
        if (nickname.equals("free")) return AnsiColors.GRAY;

        LightPlayer player = model.getPlayers().get(nickname);
        if (player == null || player.getTotemColor() == null) return AnsiColors.WHITE_BOLD;

        return switch (player.getTotemColor()) {
            case ORANGE -> AnsiColors.ORANGE;
            case WHITE  -> AnsiColors.WHITE_BOLD;
            case BLUE   -> AnsiColors.BLUE_BOLD;
            case YELLOW -> AnsiColors.YELLOW_BOLD;
            case BLACK  -> AnsiColors.BLACK_BOLD;
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