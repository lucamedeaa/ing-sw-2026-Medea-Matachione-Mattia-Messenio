package it.polimi.ingsw.client.tui.render;

import java.util.List;

public class CardBoxRenderer {

    private static final String RESET = "\033[0m";

    public static String ansiColor(Integer id) {
        if (id == null) return "\033[90m";
        if (CardNameMapper.isEvent(id))    return "\033[93m";
        if (CardNameMapper.isBuilding(id)) return "\033[96m";
        return switch (CardNameMapper.characterType(id)) {
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

        String name   = CardNameMapper.getName(id);
        String detail = CardNameMapper.getDetail(id);
        String costPp = buildingCostPp(CardNameMapper.getExtra(id), CardNameMapper.getExtra2(id));

        String eraStr = "Era " + CardNameMapper.getEra(id);

        String[] nameParts = splitName(name, 13);

        return new String[]{
                "┌─────────────┐",
                "│" + center(nameParts[0], 13) + "│",
                "│" + center(nameParts[1], 13) + "│",
                "│" + center(detail,       13) + "│",
                "│" + center(costPp,       13) + "│",
                "│" + center(eraStr,       13) + "│",
                "└─────────────┘"
        };
    }

    public static void printCardRow(List<Integer> cards) {
        if (cards == null || cards.isEmpty()) {
            System.out.println("  (no cards)");
            return;
        }
        List<String[]> boxes = cards.stream().map(CardBoxRenderer::cardBox).toList();
        for (int line = 0; line < 7; line++) {
            StringBuilder sb = new StringBuilder("  ");
            for (int i = 0; i < boxes.size(); i++) {
                String color = ansiColor(cards.get(i));
                sb.append(color).append(boxes.get(i)[line]).append(RESET);
                if (i < boxes.size() - 1) sb.append(" ");
            }
            System.out.println(sb);
        }
    }

    /** Combines "cost: 5f" + "+2 pp" → "5f +2pp", or "" if both empty. */
    private static String buildingCostPp(String extra, String extra2) {
        if (extra.isEmpty()) return "";
        String cost = extra.replace("cost: ", "");
        String pp   = extra2.replace(" ", "");
        return cost + " " + pp;
    }

    private static String[] splitName(String name, int width) {
        // split sempre prima di '(' se presente
        int paren = name.indexOf('(');
        if (paren > 0) return new String[]{name.substring(0, paren), name.substring(paren)};
        // altrimenti logica normale
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