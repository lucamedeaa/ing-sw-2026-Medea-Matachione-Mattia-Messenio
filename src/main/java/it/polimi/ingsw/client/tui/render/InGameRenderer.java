package it.polimi.ingsw.client.tui.render;

import it.polimi.ingsw.client.lightGameModel.LightGameModel;
import it.polimi.ingsw.client.lightGameModel.LightPlayer;
import it.polimi.ingsw.client.tui.OutputPort;
import it.polimi.ingsw.network.dto.AvailableActionDTO;

import java.util.List;
import java.util.Map;

public class InGameRenderer {
    private final OutputPort out;

    public InGameRenderer(OutputPort out) {
        this.out = out;
    }

    public void render(LightGameModel model, String myNickname, Map<String, int[]> deltas, String lastError) {
        out.clearScreen();
        out.print("════ MESOS — Era " + model.getCurrentEra() + " / Round " + model.getCurrentRound() + " ════\n");

        renderRows(model);
        out.print("");

        renderTracks(model);
        renderPlayersBar(model, myNickname);

        if (!myNickname.isEmpty()) {
            out.print("");
            renderPlayerTribe(model, myNickname);
        }

        renderTurnRecap(deltas, model);
        out.print("");

        List<String> logs = model.consumeGameLogs();
        if (!logs.isEmpty()) {
            out.print("── NOTIFICHE RECENTI ──");
            for (String log : logs) {
                out.print("  " + log);
            }
            out.print("");
        }

        printActions(model.getMyActions());

        if (lastError != null && !lastError.isEmpty()) {
            out.print("\033[31m[ERROR] " + lastError + "\033[0m");
        }

        if (model.isGameOver()) {
            out.print("\033[1;33m  ══ PARTITA TERMINATA — Premi INVIO per vedere i risultati ══\033[0m");
        }

        out.prompt("> ");
    }

    private void printActions(List<AvailableActionDTO> actions) {
        out.print("\n\033[1;33m── AVAILABLE ACTIONS ──> \033[0m");
        if (actions.isEmpty()) {
            out.print("  Wait for your turn...");
        } else {
            ActionRender renderer = new ActionRender(out);
            for (int i = 0; i < actions.size(); i++) {
                out.prompt("  \033[1;36m[ " + i + " ]\033[0m ");
                actions.get(i).accept(renderer);
            }
        }
        out.print("  \033[1;36m[ i ]\033[0m Guida alle carte");
        out.print("  \033[1;36m[ v <nome> ]\033[0m Guarda la tribù di un giocatore");
        out.print("  \033[1;31m[ quit ]\033[0m Chiudi definitivamente il gioco");
        out.print("  \033[1;31m[ leave ]\033[0m Abbandona la partita e torna al menu\n");
    }


    private void renderRows(LightGameModel model) {
        out.print("  UPPER ROW");
        // TODO: CardBoxRenderer andrebbe rifattorizzato in futuro per usare OutputPort
        CardBoxRenderer.printCardRow(out, model.getUpperRowCards(), true);
        out.print("");
        out.print("  LOWER ROW");
        CardBoxRenderer.printCardRow(out, model.getLowerRowCards(), true);
    }

    private void renderPlayersBar(LightGameModel model, String myNickname) {
        out.print("\033[1;37m  PLAYERS\033[0m");
        for (LightPlayer p : model.getPlayers().values()) {
            boolean isMe = p.getNickname().equals(myNickname);
            boolean isActive = p.getNickname().equals(model.getActivePlayer());

            String marker = isActive ? " \033[1;32m▶\033[0m" : "  ";
            int tribeSize = model.getTribes().getOrDefault(p.getNickname(), List.of()).size();
            String tag = isMe ? " \033[3m(you)\033[0m" : "";

            String pColor = getTotemAnsiColor(model, p.getNickname());

            out.print(String.format("%s %s%-14s\033[0m  cibo: \033[1;32m%2d\033[0m  prestigio: \033[1;32m%3d\033[0m  sconto: \033[1;32m-%d\033[0m  [%d carte]%s",
                    marker, pColor, p.getNickname(), p.getFood(), p.getPrestige(), p.getFoodDiscount(), tribeSize, tag));
        }
    }

    public void renderPlayerTribe(LightGameModel model, String nickname) {
        List<Integer> tribe = model.getTribes().getOrDefault(nickname, List.of());
        out.print("  " + nickname.toUpperCase() + "'S TRIBE");
        CardBoxRenderer.printCardRow(out, tribe, false);
    }

    private void renderTurnRecap(Map<String, int[]> deltas, LightGameModel model) {
        out.print("");
        out.print("\033[1;37m  TURN RECAP\033[0m");
        for (var e : deltas.entrySet()) {
            int df = e.getValue()[0];
            int dp = e.getValue()[1];
            int dd = e.getValue().length > 2 ? e.getValue()[2] : 0;

            String foodStr = (df >= 0 ? "+" : "") + df;
            String ppStr   = (dp >= 0 ? "+" : "") + dp;
            String discStr = dd == 0 ? "0" : (dd > 0 ? "-" + dd : "+" + Math.abs(dd));

            String pColor = getTotemAnsiColor(model, e.getKey());

            out.print(String.format("  %s%-14s\033[0m  cibo: %-3s prestigio: %-3s sconto: \033[1;32m%-3s\033[0m",
                    pColor, e.getKey(), foodStr, ppStr, discStr));
        }
    }

    private void renderTracks(LightGameModel model) {
        int pCount = Math.max(2, model.getPlayers().size());

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
            String color = getTotemAnsiColor(model, returnOccupants[i]);
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
            String color = getTotemAnsiColor(model, offerOccupants[i]);

            top.append("┌───────┐ ");
            mid1.append("│").append(specs[0]).append("│ ");
            mid2.append("│").append(specs[1]).append("│ ");
            mid3.append("│").append(color).append(player).append("\033[0m│ ");
            bot.append("└───────┘ ");
        }

        out.print(top.toString());
        out.print(mid1.toString());
        out.print(mid2.toString());
        out.print(mid3.toString());
        out.print(bot.toString());
        out.print("  * Nota: se non puoi pagare i malus in cibo, perdi 2pp per ogni cibo mancante.\n");
    }

    private String getTotemAnsiColor(LightGameModel model, String nickname) {
        if (nickname.equals("free")) return "\033[90m";

        LightPlayer player = model.getPlayers().get(nickname);
        if (player == null || player.getTotemColor() == null) return "\033[1;37m";

        return switch (player.getTotemColor()) {
            case ORANGE -> "\033[38;5;208m";
            case WHITE  -> "\033[1;37m";
            case BLUE   -> "\033[1;34m";
            case YELLOW -> "\033[1;33m";
            case BLACK  -> "\033[1;30m";
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