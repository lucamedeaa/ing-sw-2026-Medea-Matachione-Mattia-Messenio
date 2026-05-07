package it.polimi.ingsw.client.tui.render;

import it.polimi.ingsw.client.tui.OutputPort;
import java.util.List;

public class CardBoxRenderer {

    private static final String RESET = "\033[0m";

    public static String ansiColor(Integer id) {
        if (id == null) return "\033[90m";
        CardInfo info = CardNameMapper.getCard(id);

        if (info.type().equals("Event"))    return "\033[93m";
        if (info.type().equals("Building")) return "\033[96m";

        return switch (info.type()) {
            case "Builder"   -> "\033[33m";
            case "Hunter"    -> "\033[31m";
            case "Artist"    -> "\033[35m";
            case "Shaman"    -> "\033[36m";
            case "Inventor"  -> "\033[34m";
            case "Collector" -> "\033[32m";
            default          -> "\033[0m";
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

        if (showIndices) {
            StringBuilder idxBuilder = new StringBuilder("  ");
            for (int i = 0; i < cards.size(); i++) {
                idxBuilder.append("\033[1;30m").append(center("[" + i + "]", 15)).append("\033[0m");
                if (i < cards.size() - 1) idxBuilder.append(" ");
            }
            out.print(idxBuilder.toString());
        }

        List<String[]> boxes = cards.stream().map(CardBoxRenderer::cardBox).toList();
        for (int line = 0; line < 7; line++) {
            StringBuilder sb = new StringBuilder("  ");
            for (int i = 0; i < boxes.size(); i++) {
                String color = ansiColor(cards.get(i));
                sb.append(color).append(boxes.get(i)[line]).append(RESET);
                if (i < boxes.size() - 1) sb.append(" ");
            }
            out.print(sb.toString()); // Dipendenza invertita: usiamo il porto!
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