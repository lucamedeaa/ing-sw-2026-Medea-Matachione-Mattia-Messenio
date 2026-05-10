package it.polimi.ingsw.client.view.tui.render;

import it.polimi.ingsw.client.view.tui.OutputPort;
import java.util.List;

public class CardBoxRenderer {

    private static final String RESET = "AnsiColor.RESET";

    public static String ansiColor(Integer id) {
        if (id == null) return ColorAnsi.GRAY;
        CardInfo info = CardNameMapper.getCard(id);

        if (info.type().equals("Event"))    return ColorAnsi.YELLOW;
        if (info.type().equals("Building")) return ColorAnsi.CYAN;

        return switch (info.type()) {
            case "Builder"   -> ColorAnsi.YELLOW;
            case "Hunter"    -> ColorAnsi.RED;
            case "Artist"    -> ColorAnsi.MAGENTA;
            case "Shaman"    -> ColorAnsi.CYAN;
            case "Inventor"  -> ColorAnsi.BLUE;
            case "Collector" -> ColorAnsi.GREEN;
            default          -> ColorAnsi.RESET;
        };
    }

    public static String[] cardBox(Integer id) {
        if (id == null) {
            return new String[]{
                    "┌─────────────┐",
                    "│    free     │",
                    "│             │",
                    "│             │",
                    "│             │",
                    "│             │",
                    "└─────────────┘"
            };
        }

        // Usa un'unica estrazione dal DB Data-Driven
        CardInfo info = CardNameMapper.getCard(id);
        String costPp = buildingCostPp(info.cost(), info.extraPP());
        String eraStr = "Era " + info.era();
        String[] nameParts = splitName(info.name(), 13);

        return new String[]{
                "┌─────────────┐",
                "│" + center(nameParts[0], 13) + "│",
                "│" + center(nameParts[1], 13) + "│",
                "│" + center(info.detail(),  13) + "│",
                "│" + center(costPp,         13) + "│",
                "│" + center(eraStr,         13) + "│",
                "└─────────────┘"
        };
    }

    public static void printCardRow(OutputPort out, List<Integer> cards, boolean showIndices) {
        if (cards == null || cards.isEmpty()) {
            out.print("  (no cards)");
            return;
        }

        int chunkSize = 10; // Numero massimo di carte per riga
        for (int start = 0; start < cards.size(); start += chunkSize) {
            int end = Math.min(start + chunkSize, cards.size());
            List<Integer> chunk = cards.subList(start, end);

            if (showIndices) {
                StringBuilder idxBuilder = new StringBuilder("  ");
                for (int i = 0; i < chunk.size(); i++) {
                    int actualIndex = start + i;
                    idxBuilder.append(ColorAnsi.BLACK_BOLD).append(center("[" + actualIndex + "]", 15)).append(ColorAnsi.RESET);
                    if (i < chunk.size() - 1) idxBuilder.append(" ");
                }
                out.print(idxBuilder.toString());
            }

            List<String[]> boxes = chunk.stream().map(CardBoxRenderer::cardBox).toList();
            for (int line = 0; line < 7; line++) {
                StringBuilder sb = new StringBuilder("  ");
                for (int i = 0; i < boxes.size(); i++) {
                    String color = ansiColor(chunk.get(i));
                    sb.append(color).append(boxes.get(i)[line]).append(ColorAnsi.RESET);
                    if (i < boxes.size() - 1) sb.append(" ");
                }
                out.print(sb.toString());
            }
            if (end < cards.size()) out.print("");
        }
    }

    private static String buildingCostPp(String extra, String extra2) {
        if (extra.isEmpty()) return "";
        String cost = extra.replace("cost: ", "");
        String pp   = extra2.replace(" ", "");
        return cost + " " + pp;
    }

    private static String[] splitName(String name, int width) {
        int paren = name.indexOf('(');
        if (paren > 0) return new String[]{name.substring(0, paren), name.substring(paren)};
        if (name.length() <= width) return new String[]{name, ""};
        int at = name.lastIndexOf(' ', width - 1);
        if (at > 0) return new String[]{name.substring(0, at), name.substring(at + 1)};
        return new String[]{name.substring(0, width), name.substring(width)};
    }

    private static String center(String s, int width) {
        if (s.length() >= width) return s.substring(0, width);
        int pad   = width - s.length();
        int left  = pad / 2;
        int right = pad - left;
        return " ".repeat(left) + s + " ".repeat(right);
    }
}