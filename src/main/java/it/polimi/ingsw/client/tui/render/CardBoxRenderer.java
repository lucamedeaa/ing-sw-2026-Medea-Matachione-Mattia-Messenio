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
                    "┌─────────┐",
                    "│         │",
                    "│  free   │",
                    "│         │",
                    "└─────────┘"
            };
        }
        String name   = CardNameMapper.getName(id);
        String detail = CardNameMapper.getDetail(id);
        String idStr  = "#" + id;
        return new String[]{
                "┌─────────┐",
                "│" + center(name,   9) + "│",
                "│" + center(detail, 9) + "│",
                "│" + center(idStr,  9) + "│",
                "└─────────┘"
        };
    }

    public static void printCardRow(List<Integer> cards) {
        if (cards == null || cards.isEmpty()) {
            System.out.println("  (no cards)");
            return;
        }
        List<String[]> boxes = cards.stream().map(CardBoxRenderer::cardBox).toList();
        for (int line = 0; line < 5; line++) {
            StringBuilder sb = new StringBuilder("  ");
            for (int i = 0; i < boxes.size(); i++) {
                String color = ansiColor(cards.get(i));
                sb.append(color).append(boxes.get(i)[line]).append(RESET);
                if (i < boxes.size() - 1) sb.append(" ");
            }
            System.out.println(sb);
        }
    }

    private static String center(String s, int width) {
        if (s.length() >= width) return s.substring(0, width);
        int pad   = width - s.length();
        int left  = pad / 2;
        int right = pad - left;
        return " ".repeat(left) + s + " ".repeat(right);
    }
}